package de.syscy.kagecloud.wrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import de.syscy.kagecloud.ICloudNode;
import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.configuration.file.FileConfiguration;
import de.syscy.kagecloud.configuration.file.YamlConfiguration;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.packet.node.ChangeStatusPacket;
import de.syscy.kagecloud.util.Charsets;
import de.syscy.kagecloud.util.UUID;
import de.syscy.kagecloud.wrapper.network.CloudWrapperClient;
import lombok.Getter;

public class KageCloudWrapper implements ICloudNode {
	private @Getter UUID nodeId = UUID.randomUUID();
	private final @Getter String nodeName;

	private final @Getter File dataFolder;
	private final @Getter File configFile;
	private FileConfiguration config;

	private final @Getter String credentials;

	private @Getter File templatesDirectory;
	private @Getter File globalPluginDirectory;
	private @Getter File serversDirectory;

	private @Getter CloudWrapperClient client;

	private int portCounter = 15000;

	private Map<UUID, CloudServer> servers = new HashMap<>();
	private @Getter List<String> globalPlugins;

	public KageCloudWrapper() {
		KageCloud.cloudNode = this;
		KageCloud.logger = Logger.getLogger("KageCloud");

		KageCloud.dataFolder = dataFolder = new File(System.getProperty("user.dir"));
		configFile = new File(dataFolder, "config.yml");
		saveDefaultConfig();

		templatesDirectory = new File(dataFolder, "templates");
		templatesDirectory.mkdirs();
		globalPluginDirectory = new File(dataFolder, "plugins");
		globalPluginDirectory.mkdirs();
		serversDirectory = new File(dataFolder, "servers");

		if(serversDirectory.exists()) {
			try {
				FileUtils.deleteDirectory(serversDirectory);
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}

		serversDirectory.mkdirs();

		nodeName = getConfig().getString("wrapperName");
		credentials = getConfig().getString("credentials");

		globalPlugins = getConfig().getStringList("globalPlugins");

		client = new CloudWrapperClient(this);

		try {
			client.connect(getConfig().getString("coreIP", "localhost"), getConfig().getInt("port"));
		} catch(IOException ex) {
			ex.printStackTrace();

			System.exit(1);
		}
	}

	public void shutdown() {
		client.sendTCP(new ChangeStatusPacket(ServerStatus.OFFLINE));
		client.stop();

		for(CloudServer server : servers.values()) {
			server.shutdown();
		}
	}

	public void createServer(final UUID serverId, final String serverName, final String templateName) {
		final KageCloudWrapper wrapper = this;

		new Thread(new Runnable() {
			@Override
			public void run() {
				KageCloud.logger.info("Creating server with template " + templateName);

				ServerTemplate template = ServerTemplate.loadServerTemplate(wrapper, serverId, serverName, templateName);

				if(template == null) {
					KageCloud.logger.severe("Could not load template " + templateName);

					return;
				}

				CloudServer server = new CloudServer(wrapper, template);
				server.prepareServerFolder();
				server.start();

				servers.put(server.getServerId(), server);
			}
		}).start();
	}

	public void removeServer(CloudServer server) {
		servers.remove(server.getServerId());
	}

	public int getNextPort() {
		return portCounter++;
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
			URL url = KageCloudWrapper.class.getResource(filename);

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
		new KageCloudWrapper();
	}
}