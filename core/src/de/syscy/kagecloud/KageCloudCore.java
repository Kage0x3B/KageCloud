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
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

import de.syscy.kagecloud.configuration.file.FileConfiguration;
import de.syscy.kagecloud.configuration.file.YamlConfiguration;
import de.syscy.kagecloud.network.CloudConnection;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.CloudCoreConnection;
import de.syscy.kagecloud.network.KryoServer;
import de.syscy.kagecloud.network.packet.CreateServerPacket;
import de.syscy.kagecloud.network.packet.node.RegisterServerPacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveNetworkPacket;
import de.syscy.kagecloud.network.packet.proxy.AddServerPacket;
import de.syscy.kagecloud.network.packet.proxy.RemoveServerPacket;
import de.syscy.kagecloud.plugin.PluginManager;
import de.syscy.kagecloud.scheduler.CloudScheduler;
import de.syscy.kagecloud.scheduler.TaskScheduler;
import de.syscy.kagecloud.util.BasicServerController;
import de.syscy.kagecloud.util.Charsets;
import de.syscy.kagecloud.util.PlayerAmountUpdater;
import de.syscy.kagecloud.util.ServerController;
import de.syscy.kagecloud.util.UUID;
import lombok.Getter;

public class KageCloudCore implements ICloudNode {
	private @Getter UUID nodeId = new UUID(0, 0);
	private @Getter String nodeName = "core";

	private final @Getter File dataFolder;
	private final File configFile;
	private FileConfiguration config;

	private KryoServer server;
	private @Getter String credentials;

	private final @Getter Logger logger;

	public final @Getter PluginManager pluginManager;
	private final @Getter File pluginsFolder;

	private final @Getter TaskScheduler scheduler = new CloudScheduler();

	private @Getter Map<UUID, CloudConnection> bungeeCordProxies = new HashMap<>();
	private @Getter Map<UUID, CloudConnection> wrappers = new HashMap<>();
	private @Getter Map<UUID, CloudServer> servers = new HashMap<>();

	private int serverNameCounter = 1;

	private @Getter List<String> startingServerTemplates = new ArrayList<>();

	private @Getter Map<String, ServerController> serverControllers = new HashMap<>();

	private @Getter Map<UUID, CloudPlayer> players = new HashMap<>();

	public KageCloudCore() {
		// Java uses ! to indicate a resource inside of a jar/zip/other container. Running KageCloud from within a directory that has a ! will cause this to muck up.
		Preconditions.checkState(new File(".").getAbsolutePath().indexOf('!') == -1, "Cannot use KageCloud in zip/jar/other container file.");

		KageCloud.cloudNode = this;
		KageCloud.logger = logger = Logger.getLogger("KageCloud");

		KageCloud.dataFolder = dataFolder = new File(System.getProperty("user.dir"));
		getDataFolder().mkdirs();

		pluginsFolder = new File(dataFolder, "plugins");
		pluginsFolder.mkdirs();

		pluginManager = new PluginManager(this);
		pluginManager.detectPlugins(pluginsFolder);
		pluginManager.loadPlugins();
		pluginManager.enablePlugins();

		configFile = new File(dataFolder, "config.yml");
		saveDefaultConfig();

		credentials = getConfig().getString("credentials");

		addServerController(new BasicServerController("lobby", 1, 10, 75));

		scheduler.schedule(pluginManager.getCorePlugin(), new PlayerAmountUpdater(this), 10, 10, TimeUnit.SECONDS);

		server = new KryoServer();

		try {
			server.start(this, getConfig().getInt("port"));
		} catch(IOException ex) {
			KageCloud.logger.severe("Could not start cloud server");

			ex.printStackTrace();

			System.exit(1);
		}
	}

	public void shutdown() {
		shutdown("Restarting network"); //TODO: Translate message
	}

	public void shutdown(String reason) {
		KageCloud.logger.info("Disabling plugins");
		pluginManager.disablePlugins();

		KageCloud.logger.info("Shutting down BungeeCord proxies...");

		for(CloudConnection bungeeCordProxy : bungeeCordProxies.values()) {
			bungeeCordProxy.shutdown();
		}

		KageCloud.logger.info("Shutting down servers...");

		for(CloudServer cloudServer : servers.values()) {
			cloudServer.getConnection().shutdown();
		}

		KageCloud.logger.info("Shutting down wrappers...");

		for(CloudConnection wrapperConnection : wrappers.values()) {
			wrapperConnection.shutdown();
		}
	}

	public void onPlayerJoin(CloudCoreConnection proxyConnection, PlayerJoinNetworkPacket packet) {
		CloudPlayer player = new CloudPlayer(UUID.fromString(packet.getId()), packet.getName(), packet.getVersion(), proxyConnection);
		players.put(player.getId(), player);
	}

	public void onPlayerLeave(PlayerLeaveNetworkPacket packet) {
		UUID id = UUID.fromString(packet.getId());
		CloudPlayer player = players.remove(id);

		if(player != null && player.getCurrentServer() != null) {
			player.getCurrentServer().getPlayers().remove(id);
		}
	}

	public boolean createServer(String templateName) {
		UUID serverId = UUID.randomUUID();
		String serverName = getServerName(templateName);

		CloudConnection wrapperConnection = getAvailableWrapper();

		if(wrapperConnection != null) {
			startingServerTemplates.add(templateName);
			wrapperConnection.sendTCP(new CreateServerPacket(serverId, templateName, serverName));

			return true;
		} else {
			KageCloud.logger.warning("No wrapper available for starting a " + templateName + " server");
		}

		return false;
	}

	public void addServer(CloudCoreConnection connection, RegisterServerPacket packet) {
		InetSocketAddress serverAddress = new InetSocketAddress(connection.getRemoteAddressTCP().getHostString(), packet.getPort());

		CloudServer server = new CloudServer(connection, packet.getName(), serverAddress, false, packet.getTemplateName(), packet.isLobby());
		connection.setConnectionRepresentation(server);

		startingServerTemplates.remove(packet.getTemplateName());

		servers.put(packet.getId(), server);

		for(CloudConnection proxy : bungeeCordProxies.values()) {
			proxy.sendTCP(new AddServerPacket(server.getConnection().getNodeId(), server.getName(), server.getAddress().getPort(), server.getTemplateName(), server.isLobby()));
		}

		KageCloud.logger.info("Server registered: " + packet.getName() + " (" + connection.getRemoteAddressTCP() + ":" + packet.getPort() + ")");
	}

	public void removeServer(UUID serverId) {
		CloudServer serverInfo = servers.remove(serverId);

		if(serverInfo != null) {
			CloudConnection connection = serverInfo.getConnection();

			for(CloudConnection proxy : bungeeCordProxies.values()) {
				proxy.sendTCP(new RemoveServerPacket(serverId));
			}

			if(connection != null && connection.isConnected()) {
				connection.shutdown();
			}
		}
	}

	public void addProxy(UUID proxyId, CloudCoreConnection connection) {
		KageCloud.logger.info("New BungeeCord proxy " + connection.getName() + " (" + proxyId + ")");

		for(CloudServer cloudServer : servers.values()) {
			connection.sendTCP(new AddServerPacket(cloudServer.getConnection().getNodeId(), cloudServer.getName(), cloudServer.getAddress().getPort(), cloudServer.getTemplateName(), cloudServer.isLobby()));
		}

		bungeeCordProxies.put(proxyId, connection);
	}

	public void removeProxy(UUID proxyId) {
		KageCloud.logger.info("Removed proxy " + proxyId);

		CloudConnection proxyConnection = bungeeCordProxies.remove(proxyId);

		if(proxyConnection != null) {
			proxyConnection.shutdown();
		}
	}

	public void addWrapper(UUID wrapperId, CloudCoreConnection connection) {
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

		serverControllers.put(serverController.getTemplateName().toLowerCase(), serverController);

		serverController.setTask(scheduler.schedule(pluginManager.getCorePlugin(), serverController, 5, 5, TimeUnit.SECONDS));
	}

	public void removeServerController(String templateName) {
		ServerController serverController = serverControllers.remove(templateName.toLowerCase());

		if(serverController != null) {
			serverController.getTask().cancel();
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

		for(CloudServer serverInfo : servers.values()) {
			if(serverInfo.getTemplateName().equals(templateName)) {
				amount++;
			}
		}

		return amount;
	}

	public List<CloudServer> getJoinableLobbyServers(CloudPlayer player) {
		List<CloudServer> joinableServers = new ArrayList<>();

		for(CloudServer serverInfo : servers.values()) {
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

	public FileConfiguration getConfig() {
		if(config == null) {
			reloadConfig();
		}

		return config;
	}

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);

		final InputStream defConfigStream = getResource("/config.yml");

		if(defConfigStream == null) {
			KageCloud.logger.warning("No default config included");

			return;
		}

		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
	}

	public void saveConfig() {
		try {
			getConfig().save(configFile);
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
			URL url = KageCloudCore.class.getResource(filename);

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

	public static void main(String[] args) {
		new KageCloudCore();
	}
}