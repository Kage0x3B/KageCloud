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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import de.syscy.kagecloud.command.CreateServerCommand;
import de.syscy.kagecloud.event.ServerCreateEvent;
import de.syscy.kagecloud.event.ServerStartedEvent;
import de.syscy.kagecloud.event.ServerStoppedEvent;
import de.syscy.kagecloud.network.CloudConnection;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.CloudCoreServer;
import de.syscy.kagecloud.network.packet.CreateServerPacket;
import de.syscy.kagecloud.network.packet.RegisterServerPacket;
import de.syscy.kagecloud.util.BasicServerController;
import de.syscy.kagecloud.util.Charsets;
import de.syscy.kagecloud.util.CloudReconnectHandler;
import de.syscy.kagecloud.util.ServerController;
import de.syscy.kagecloud.util.UUID;
import lombok.Getter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class KageCloudBungee extends Plugin implements ICloudNode {
	private @Getter UUID nodeId = new UUID(0, 0);
	private @Getter String nodeName = "core";

	private @Getter File configFile;
	private Configuration config;

	private CloudCoreServer server;
	private @Getter String credentials;

	private @Getter Map<UUID, CloudServerInfo> servers = new HashMap<>();
	private Map<UUID, CloudConnection> wrappers = new HashMap<>();

	private int serverNameCounter = 1;

	private @Getter List<String> startingServerTemplates = new ArrayList<>();

	private @Getter Map<String, ServerController> serverControllers = new HashMap<>();

	@Override
	@SuppressWarnings("deprecation")
	public void onEnable() {
		KageCloud.cloudNode = this;
		KageCloud.logger = getLogger();
		KageCloud.dataFolder = getDataFolder();

		getDataFolder().mkdirs();
		configFile = new File(getDataFolder(), "config.yml");

		credentials = getConfig().getString("credentials");

		server = new CloudCoreServer();

		try {
			server.start(this, getConfig().getInt("port"));
		} catch(IOException ex) {
			KageCloud.logger.severe("Could not start cloud server");

			ex.printStackTrace();

			BungeeCord.getInstance().stop();
		}

		getProxy().setReconnectHandler(new CloudReconnectHandler(this));
		getProxy().getPluginManager().registerCommand(this, new CreateServerCommand(this));

		getProxy().getServers().clear();
	}

	@Override
	public void onDisable() {
		KageCloud.logger.info("Shutting down servers...");

		for(CloudServerInfo cloudServer : servers.values()) {
			cloudServer.getConnection().shutdown();
		}

		KageCloud.logger.info("Shutting down wrappers...");

		for(CloudConnection wrapperConnection : wrappers.values()) {
			wrapperConnection.shutdown();
		}
	}

	public boolean createServer(String templateName) {
		UUID serverId = UUID.randomUUID();
		String serverName = getServerName(templateName);

		CloudConnection wrapperConnection = getAvailableWrapper();

		if(wrapperConnection != null) {
			if(!getProxy().getPluginManager().callEvent(new ServerCreateEvent(templateName)).isCancelled()) {
				startingServerTemplates.add(templateName);
				wrapperConnection.sendTCP(new CreateServerPacket(serverId, templateName, serverName));

				return true;
			}
		} else {
			KageCloud.logger.warning("No wrapper available for starting a " + templateName + " server");
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	public void addServerInfo(CloudConnection connection, RegisterServerPacket packet) {
		InetSocketAddress serverAddress = new InetSocketAddress(connection.getRemoteAddressTCP().getHostString(), packet.getPort());

		CloudServerInfo serverInfo = new CloudServerInfo(connection, packet.getName(), serverAddress, packet.getName(), false, packet.getTemplateName(), packet.isLobby());
		connection.setServerInfo(serverInfo);

		startingServerTemplates.remove(packet.getTemplateName());

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

			CloudConnection connection = serverInfo.getConnection();

			if(connection != null && connection.isConnected()) {
				connection.shutdown();
			}
		}
	}

	public void addWrapper(UUID wrapperId, CloudConnection connection) {
		KageCloud.logger.info("New wrapper " + connection.getName() + " (" + wrapperId + ")");

		wrappers.put(wrapperId, connection);
	}

	public void removeWrapper(UUID wrapperId) {
		KageCloud.logger.info("Removed wrapper " + wrapperId);

		CloudConnection wrapperConnection = wrappers.remove(wrapperId);

		if(wrapperConnection != null) {
			wrapperConnection.shutdown();
		}
	}

	public void createServerController(String templateName, int minAvailableServers, int maxServers, int serverPlayerMaximum) {
		addServerController(new BasicServerController(templateName, minAvailableServers, maxServers, serverPlayerMaximum));
	}

	public void addServerController(ServerController serverController) {
		serverController.setPlugin(this);

		ScheduledTask serverControllerTask = getProxy().getScheduler().schedule(this, serverController, 10, 10, TimeUnit.SECONDS);
		serverController.setScheduledTask(serverControllerTask);
		serverControllers.put(serverController.getTemplateName(), serverController);
	}

	public void removeServerController(String templateName) {
		ScheduledTask serverControllerTask = serverControllers.remove(templateName.toLowerCase());

		if(serverControllerTask != null) {
			serverControllerTask.cancel();
		}
	}

	private String getServerName(String templateName) {
		return templateName + serverNameCounter++;
	}

	public int getCurrentServerAmount(String templateName) {
		int amount = 0;

		for(String startingServerTemplate : startingServerTemplates) {
			if(startingServerTemplate.equals(templateName)) {
				amount++;
			}
		}

		for(CloudServerInfo serverInfo : servers.values()) {
			if(serverInfo.getTemplateName().equals(templateName)) {
				amount++;
			}
		}

		return amount;
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

	private CloudConnection getAvailableWrapper() {
		for(CloudConnection wrapperConnection : wrappers.values()) {
			if(wrapperConnection.getServerStatus().equals(ServerStatus.RUNNING)) {
				return wrapperConnection;
			}
		}

		return null;
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