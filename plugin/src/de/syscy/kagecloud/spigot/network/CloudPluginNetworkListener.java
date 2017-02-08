package de.syscy.kagecloud.spigot.network;

import org.bukkit.Bukkit;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ReflectionListener;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.packet.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.ExecuteCommandPacket;
import de.syscy.kagecloud.network.packet.RegisterServerPacket;
import de.syscy.kagecloud.network.packet.ShutdownPacket;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CloudPluginNetworkListener extends ReflectionListener {
	private final KageCloudSpigot plugin;

	@Override
	public void connected(final Connection connection) {
		KageCloud.logger.info("Connected to cloud core, registering server...");
		connection.sendTCP(new RegisterServerPacket(plugin.getNodeId(), plugin.getNodeName(), plugin.getCredentials(), Bukkit.getPort(), plugin.getTemplateName(), plugin.isLobbyServer()));
		connection.sendTCP(new ChangeStatusPacket(ServerStatus.STARTING));

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { //This executes when all plugins are loaded
			@Override
			public void run() {
				connection.sendTCP(new ChangeStatusPacket(ServerStatus.RUNNING));
			}
		});
	}

	public void received(Connection connection, ExecuteCommandPacket packet) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), packet.getCommand());
	}

	public void received(Connection connection, ShutdownPacket packet) {
		Bukkit.shutdown();
	}
}