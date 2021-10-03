package de.syscy.kagecloud.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ReflectionListener;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.MessagePosition;
import de.syscy.kagecloud.CloudServerInfo;
import de.syscy.kagecloud.KageCloudVelocity;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.packet.PluginDataPacket;
import de.syscy.kagecloud.network.packet.info.PlayerAmountPacket;
import de.syscy.kagecloud.network.packet.info.UpdatePingDataPacket;
import de.syscy.kagecloud.network.packet.node.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.node.RegisterProxyPacket;
import de.syscy.kagecloud.network.packet.node.ShutdownPacket;
import de.syscy.kagecloud.network.packet.player.*;
import de.syscy.kagecloud.network.packet.proxy.AddServerPacket;
import de.syscy.kagecloud.network.packet.proxy.RemoveServerPacket;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CloudProxyNetworkListener extends ReflectionListener {
	private final KageCloudVelocity plugin;

	@Override
	public void connected(Connection connection) {
		connection.sendTCP(new RegisterProxyPacket(plugin.getNodeId(), plugin.getNodeName(), plugin.getCredentials()));
		connection.sendTCP(new ChangeStatusPacket(ServerStatus.RUNNING));
	}

	@Override
	public void disconnected(Connection connection) {
		plugin.connectToCore(); //Reconnect
	}

	public void received(Connection connection, ShutdownPacket packet) {
		plugin.connectToCore(); //Reconnecting right now because the BungeeCord proxy should never be shutdown TODO
		//		BungeeCord.getInstance().stop(packet.getReason() != null ? packet.getReason() : "Shutting down network.");
	}

	public void received(Connection connection, AddServerPacket packet) {
		plugin.addServerInfo(connection, packet);
	}

	public void received(Connection connection, RemoveServerPacket packet) {
		plugin.removeServer(packet.getId());
	}

	public void received(Connection connection, KickPlayerPacket packet) {
		Optional<Player> player = plugin.getProxy().getPlayer(UUID.fromString(packet.getPlayerId()));

		player.ifPresent(value -> value.disconnect(GsonComponentSerializer.gson().deserialize(packet.getJsonReason())));
	}

	public void received(Connection connection, MessagePacket packet) {
		Optional<Player> player = plugin.getProxy().getPlayer(UUID.fromString(packet.getReceiverId()));

		if(player.isPresent()) {
			MessageType messageType = MessageType.values()[packet.getType().ordinal()];
			player.get().sendMessage(GsonComponentSerializer.gson().deserialize(packet.getJsonMessage()), messageType);
		}
	}

	public void received(Connection connection, ConnectPlayerPacket packet) {
		Optional<Player> player = plugin.getProxy().getPlayer(UUID.fromString(packet.getPlayerId()));
		Optional<RegisteredServer> serverInfo = getServer(packet.getServerName(), packet.isExactServer());

		if(player.isPresent() && serverInfo.isPresent()) {
			player.get().createConnectionRequest(serverInfo.get()).fireAndForget();
		}
	}

	public void received(Connection connection, ConnectPlayerIDPacket packet) {
		Optional<Player> player = plugin.getProxy().getPlayer(UUID.fromString(packet.getPlayerId()));
		Optional<RegisteredServer> serverInfo = getServer(packet.getServerName(), packet.isExactServer());

		if(player.isPresent() && serverInfo.isPresent()) {
			player.get().createConnectionRequest(serverInfo.get()).connectWithIndication().thenAccept(result -> {
				ConnectedServerInfoPacket response = new ConnectedServerInfoPacket(result ? serverInfo.get().getServerInfo().getName() : "null");
				response.setId(packet.getId());

				plugin.getClient().sendTCP(response.buildResponse(packet));
			});
		}
	}

	private Optional<RegisteredServer> getServer(String serverName, boolean exactServer) {
		if(exactServer) {
			return plugin.getProxy().getServer(serverName);
		} else {
			Optional<CloudServerInfo> serverInfo = plugin.getServers().values().stream().filter(s -> s.getTemplateName().equalsIgnoreCase(serverName)).findAny();
			return serverInfo.flatMap(s -> plugin.getProxy().getServer(s.getName()));
		}
	}

	public void received(Connection connection, PluginDataPacket packet) {
		plugin.onPluginData(connection, packet);
	}

	public void received(Connection connection, PlayerAmountPacket packet) {
		for(Entry<de.syscy.kagecloud.util.UUID, Integer> amountEntry : packet.getPlayerAmount().entrySet()) {
			CloudServerInfo serverInfo = plugin.getServers().get(amountEntry.getKey());

			if(serverInfo != null) {
				serverInfo.setActualPlayers(amountEntry.getValue());
			}
		}
	}

	public void received(Connection connection, UpdatePingDataPacket packet) {
		plugin.getPingListener().updateFromPacket(packet);
	}
}