package de.syscy.kagecloud;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.scheduler.ScheduledTask;
import de.syscy.kagecloud.configuration.file.YamlConfiguration;
import de.syscy.kagecloud.event.ServerStartedEvent;
import de.syscy.kagecloud.event.ServerStoppedEvent;
import de.syscy.kagecloud.listener.CloudListener;
import de.syscy.kagecloud.listener.PingListener;
import de.syscy.kagecloud.network.ChunkedPacketListener;
import de.syscy.kagecloud.network.CloudProxyNetworkListener;
import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.network.packet.PluginDataPacket;
import de.syscy.kagecloud.network.packet.proxy.AddServerPacket;
import de.syscy.kagecloud.util.Charsets;
import de.syscy.kagecloud.util.CloudCoreConnectRunnable;
import de.syscy.kagecloud.util.ICloudPluginDataListener;
import de.syscy.kagecloud.util.UUID;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Plugin(id = "kagecloud-velocity", name = "KageCloud Velocity", version = "1.0", description = "KageCloud Proxy plugin", authors = { "Kage0x3B" })
public class KageCloudVelocity implements ICloudNode {
	private @Getter UUID nodeId = UUID.randomUUID();
	private @Getter String nodeName = "bungee-" + nodeId;

	@Inject private @Getter ProxyServer proxy;
	@Inject private @Getter Logger logger;
	@Inject private @DataDirectory @Getter Path dataFolderPath;
	private @Getter File dataFolder;

	private @Getter File configFile;
	private YamlConfiguration config;

	private @Getter Client client;
	private @Getter boolean shutdown;
	private @Getter String credentials;

	private @Getter Map<UUID, CloudServerInfo> servers = new HashMap<>();
	private @Getter Map<ServerInfo, CloudServerInfo> serverLookup = new HashMap<>();

	private Map<String, ICloudPluginDataListener> pluginDataListeners = new HashMap<>();

	private @Getter CloudListener mainListener;
	private @Getter PingListener pingListener;

	@Subscribe
	public void onProxyInitialize(ProxyInitializeEvent event) {
		KageCloud.cloudNode = this;
		KageCloud.logger = getLogger();
		KageCloud.dataFolder = dataFolder = dataFolderPath.toFile();
		dataFolder.mkdirs();

		configFile = new File(getDataFolder(), "config.yml");
		if(!configFile.exists()) {
			saveDefaultConfig();
		}

		credentials = getConfig().getString("credentials");

		client = new Client();
		Packet.registerKryoClasses(client.getKryo());
		Listener listener = new CloudProxyNetworkListener(this);
		client.addListener(new ChunkedPacketListener(listener));
		client.addListener(listener);
		client.start();

		getProxy().getEventManager().register(this, mainListener = new CloudListener(this));
		getProxy().getEventManager().register(this, pingListener = new PingListener(this));

		connectToCore();
	}

	@Subscribe
	public void onProxyShutdown(ProxyShutdownEvent event) {
		shutdown = true;

		client.close();
	}

	public void connectToCore() {
		if(client.isConnected()) {
			KageCloud.logger.info("Called connectToCore even though the client is already connected?..");

			return;
		}

		if(shutdown) {
			return;
		}

		CloudCoreConnectRunnable connectRunnable = new CloudCoreConnectRunnable(this);
		ScheduledTask scheduledTask = getProxy().getScheduler().buildTask(this, connectRunnable).repeat(10, TimeUnit.SECONDS).schedule();
		connectRunnable.setTask(scheduledTask);
	}

	public boolean isConnected() {
		return client != null && client.isConnected();
	}

	public boolean isMaintenanceMode() {
		return getConfig().getBoolean("maintenanceMode", false) || !isConnected();
	}

	public void registerPluginDataListener(String channel, ICloudPluginDataListener listener) {
		pluginDataListeners.put(channel.toLowerCase(), listener);
	}

	public void onPluginData(Connection sender, PluginDataPacket packet) {
		if(pluginDataListeners.containsKey(packet.getChannel().toLowerCase())) {
			pluginDataListeners.get(packet.getChannel().toLowerCase()).onPluginData(sender, packet);
		}
	}

	public void addServerInfo(Connection connection, AddServerPacket packet) {
		InetSocketAddress serverAddress = new InetSocketAddress(connection.getRemoteAddressTCP().getHostString(), packet.getPort());

		CloudServerInfo serverInfo = new CloudServerInfo(connection, packet.getId(), packet.getName(), serverAddress, packet.getTemplateName(), packet.isLobby());

		servers.put(packet.getId(), serverInfo);
		serverLookup.put(serverInfo.getVelocityServerInfo(), serverInfo);
		getProxy().registerServer(serverInfo.getVelocityServerInfo());

		getProxy().getEventManager().fireAndForget(new ServerStartedEvent(serverInfo));

		KageCloud.logger.info("Server registered: " + packet.getName() + " (" + connection.getRemoteAddressTCP() + ":" + packet.getPort() + ")");
	}

	public void removeServer(UUID serverId) {
		CloudServerInfo serverInfo = servers.remove(serverId);

		if(serverInfo != null) {
			serverLookup.remove(serverInfo.getVelocityServerInfo());
			getProxy().unregisterServer(serverInfo.getVelocityServerInfo());

			getProxy().getEventManager().fireAndForget(new ServerStoppedEvent(serverInfo));
		}
	}

	public List<CloudServerInfo> getJoinableLobbyServers(Player player) {
		return servers.values().parallelStream().filter(CloudServerInfo::isLobby).collect(Collectors.toList());
	}

	public YamlConfiguration getConfig() {
		if(config == null) {
			reloadConfig();
		}

		return config;
	}

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);

		final InputStream defConfigStream = getResource("/config.yml");

		if(defConfigStream == null) {
			KageCloud.logger.warn("No default config included");

			return;
		}

		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
	}

	public void saveConfig() {
		try {
			getConfig().save(configFile);
		} catch(IOException ex) {
			KageCloud.logger.error("Could not save config to " + configFile, ex);
		}
	}

	public void saveDefaultConfig() {
		if(!configFile.exists()) {
			saveResource("/config.yml", false);
		}
	}

	public void saveResource(String resourcePath, boolean replace) {
		if(resourcePath == null || resourcePath.equals("")) {
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
		}

		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = getResource(resourcePath);

		if(in == null) {
			throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
		}

		File outFile = new File(dataFolder, resourcePath);
		int lastIndex = resourcePath.lastIndexOf('/');
		File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

		if(!outDir.exists()) {
			outDir.mkdirs();
		}

		try {
			if(!outFile.exists() || replace) {
				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				while((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			} else {
				KageCloud.logger.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
			}
		} catch(IOException ex) {
			KageCloud.logger.error("Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

	public InputStream getResource(String filename) {
		if(filename == null) {
			throw new IllegalArgumentException("Filename cannot be null");
		}

		try {
			URL url = KageCloudVelocity.class.getResource(filename);

			if(url == null) {
				return null;
			}

			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch(IOException ex) {
			return null;
		}
	}
}