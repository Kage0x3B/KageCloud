package de.syscy.kagecloud;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;

import de.syscy.kagecloud.event.ServerStartedEvent;
import de.syscy.kagecloud.event.ServerStoppedEvent;
import de.syscy.kagecloud.network.CloudProxyNetworkListener;
import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.network.packet.PluginDataPacket;
import de.syscy.kagecloud.network.packet.proxy.AddServerPacket;
import de.syscy.kagecloud.util.Charsets;
import de.syscy.kagecloud.util.CloudReconnectHandler;
import de.syscy.kagecloud.util.ICloudPluginDataListener;
import de.syscy.kagecloud.util.UUID;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class KageCloudBungee extends Plugin implements ICloudNode {
	private @Getter UUID nodeId = UUID.randomUUID();
	private @Getter String nodeName = "bungee-" + nodeId;

	private @Getter File configFile;
	private Configuration config;

	private @Getter Client client;
	private @Getter String credentials;

	private @Getter Map<UUID, CloudServerInfo> servers = new HashMap<>();

	private Map<String, ICloudPluginDataListener> pluginDataListeners = new HashMap<>();

	@Override
	@SuppressWarnings("deprecation")
	public void onEnable() {
		KageCloud.cloudNode = this;
		KageCloud.logger = getLogger();
		KageCloud.dataFolder = getDataFolder();

		getDataFolder().mkdirs();
		configFile = new File(getDataFolder(), "config.yml");

		credentials = getConfig().getString("credentials");

		client = new Client();
		Packet.registerKryoClasses(client.getKryo());
		client.addListener(new CloudProxyNetworkListener(this));

		client.start();

		try {
			client.connect(5000, getConfig().getString("coreIP", "localhost"), getConfig().getInt("port"));

			KageCloud.logger.info("Connected to " + getConfig().getString("coreIP", "localhost"));
		} catch(IOException ex) {
			ex.printStackTrace();

			getProxy().stop("Could not connect to KageCloud core server!");
		}

		getProxy().setReconnectHandler(new CloudReconnectHandler(this));

		getProxy().getPluginManager().registerListener(this, new CloudListener(this));

		getProxy().getServers().clear();
	}

	@Override
	public void onDisable() {
		client.close();
	}

	public void registerPluginDataListener(String channel, ICloudPluginDataListener listener) {
		pluginDataListeners.put(channel.toLowerCase(), listener);
	}

	public void onPluginData(Connection sender, PluginDataPacket packet) {
		if(pluginDataListeners.containsKey(packet.getChannel().toLowerCase())) {
			pluginDataListeners.get(packet.getChannel().toLowerCase()).onPluginData(sender, packet);
		}
	}

	@SuppressWarnings("deprecation")
	public void addServerInfo(Connection connection, AddServerPacket packet) {
		InetSocketAddress serverAddress = new InetSocketAddress(connection.getRemoteAddressTCP().getHostString(), packet.getPort());

		CloudServerInfo serverInfo = new CloudServerInfo(connection, packet.getId(), packet.getName(), serverAddress, packet.getName(), false, packet.getTemplateName(), packet.isLobby());

		servers.put(packet.getId(), serverInfo);
		getProxy().getServers().put(packet.getName(), serverInfo);

		getProxy().getPluginManager().callEvent(new ServerStartedEvent(serverInfo));

		KageCloud.logger.info("Server registered: " + packet.getName() + " (" + connection.getRemoteAddressTCP() + ":" + packet.getPort() + ")");
	}

	@SuppressWarnings("deprecation")
	public void removeServer(UUID serverId) {
		CloudServerInfo serverInfo = servers.remove(serverId);

		if(serverInfo != null) {
			getProxy().getServers().remove(serverInfo.getName());

			getProxy().getPluginManager().callEvent(new ServerStoppedEvent(serverInfo));
		}
	}

	public List<CloudServerInfo> getJoinableLobbyServers(ProxiedPlayer player) {
		List<CloudServerInfo> joinableServers = new ArrayList<>();

		for(CloudServerInfo serverInfo : servers.values()) {
			if(serverInfo.isLobby() && serverInfo.canAccess(player)) {
				joinableServers.add(serverInfo);
			}
		}

		return joinableServers;
	}

	public Configuration getConfig() {
		if(config == null) {
			reloadConfig();
		}

		return config;
	}

	public void reloadConfig() {
		final InputStream defConfigStream = getResource("/config.yml");

		try {
			saveDefaultConfig();

			if(defConfigStream != null) {
				Configuration defConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new InputStreamReader(defConfigStream, Charsets.UTF_8));
				config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile, defConfig);
			} else {
				config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public void saveConfig() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
		} catch(IOException ex) {
			KageCloud.logger.log(Level.SEVERE, "Could not save config to " + configFile, ex);
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

		File outFile = new File(getDataFolder(), resourcePath);
		int lastIndex = resourcePath.lastIndexOf('/');
		File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

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
				KageCloud.logger.log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
			}
		} catch(IOException ex) {
			KageCloud.logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

	public InputStream getResource(String filename) {
		if(filename == null) {
			throw new IllegalArgumentException("Filename cannot be null");
		}

		try {
			URL url = getClass().getResource(filename);

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