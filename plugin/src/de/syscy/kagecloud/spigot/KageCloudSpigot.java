package de.syscy.kagecloud.spigot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.esotericsoftware.kryonet.Connection;

import de.syscy.kagecloud.ICloudNode;
import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.packet.PluginDataPacket;
import de.syscy.kagecloud.network.packet.node.ChangeStatusPacket;
import de.syscy.kagecloud.spigot.command.CloudCommandManager;
import de.syscy.kagecloud.spigot.network.CloudPluginClient;
import de.syscy.kagecloud.util.ICloudPluginDataListener;
import de.syscy.kagecloud.util.UUID;
import lombok.Getter;

public class KageCloudSpigot extends JavaPlugin implements ICloudNode, Listener {
	private @Getter String nodeName;
	private @Getter UUID nodeId;

	private @Getter String templateName;
	private @Getter String wrapperName;
	private @Getter boolean lobbyServer;

	private @Getter String credentials;

	private @Getter CloudPluginClient client;

	private Map<String, ICloudPluginDataListener> pluginDataListeners = new HashMap<>();

	@Override
	public void onEnable() {
		KageCloud.cloudNode = this;
		KageCloud.logger = getLogger();
		KageCloud.dataFolder = getDataFolder();

		String serverIdString = System.getenv("serverId");

		if(serverIdString == null) {
			KageCloud.logger.severe("Please start the server using KageCloud.");

			Bukkit.shutdown();

			return;
		}

		nodeId = UUID.fromString(serverIdString);
		nodeName = System.getenv("serverName");
		templateName = System.getenv("templateName");
		wrapperName = System.getenv("wrapperName");
		lobbyServer = Boolean.parseBoolean(System.getenv("isLobbyServer"));

		credentials = getConfig().getString("credentials");

		client = new CloudPluginClient(this);

		try {
			client.connect(getConfig().getString("coreIP", "localhost"), getConfig().getInt("port"));
		} catch(IOException ex) {
			ex.printStackTrace();

			System.exit(1);
		}

		Bukkit.getPluginManager().registerEvents(this, this);

		CloudCommandManager cloudCommandManager = new CloudCommandManager(this);
		getCommand("cloud").setExecutor(cloudCommandManager);
		getCommand("cloud").setTabCompleter(cloudCommandManager);
	}

	@Override
	public void onDisable() {
		client.sendTCP(new ChangeStatusPacket(ServerStatus.OFFLINE));
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
}