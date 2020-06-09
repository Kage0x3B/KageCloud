package de.syscy.kagecloud.spigot.network;

import java.util.HashMap;
import java.util.Map;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.packet.IDPacket;
import de.syscy.kagecloud.network.packet.PluginDataPacket;
import de.syscy.kagecloud.network.packet.node.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.node.RegisterServerPacket;
import de.syscy.kagecloud.network.packet.node.ShutdownPacket;
import de.syscy.kagecloud.network.packet.server.ExecuteCommandPacket;
import de.syscy.kagecloud.network.packet.server.ReloadServerPacket;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagecloud.util.UUID;

import org.bukkit.Bukkit;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ReflectionListener;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CloudPluginNetworkListener extends ReflectionListener {
	private final KageCloudSpigot plugin;

	private Map<UUID, IDPacketListener<?>> idPacketListeners = new HashMap<>();

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

	@Override
	public void received(Connection connection, Object object) {
		super.received(connection, object);

		if(object instanceof IDPacket) {
			IDPacket packet = (IDPacket) object;

			IDPacketListener<?> listener = idPacketListeners.remove(packet.getId());

			if(listener != null) {
				listener.received0(connection, packet);
			}
		}
	}

	public void received(Connection connection, ExecuteCommandPacket packet) {
		Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), packet.getCommand()));
	}

	public void received(Connection connection, ShutdownPacket packet) {
		if(plugin.isEnabled()) {
			Bukkit.getScheduler().runTask(plugin, () -> Bukkit.shutdown());
		}
	}

	public void received(Connection connection, ReloadServerPacket packet) {
		if(plugin.isEnabled()) {
			Bukkit.getScheduler().runTask(plugin, () -> Bukkit.reload());
		}
	}

	public void received(Connection connection, PluginDataPacket packet) {
		plugin.onPluginData(connection, packet);
	}

	public void addIDPacketListener(IDPacket packet, IDPacketListener<?> listener) {
		idPacketListeners.put(packet.prepareIDPacket(plugin.getNodeId()), listener);
	}

	public static interface IDPacketListener<T extends IDPacket> {
		default public void received0(Connection connection, IDPacket packet) {
			received(connection, (T) packet);
		};

		public void received(Connection connection, T packet);
	}
}