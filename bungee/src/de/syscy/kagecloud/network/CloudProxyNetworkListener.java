package de.syscy.kagecloud.network;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import de.syscy.kagecloud.CloudServerInfo;
import de.syscy.kagecloud.KageCloudBungee;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.network.packet.PluginDataPacket;
import de.syscy.kagecloud.network.packet.info.PlayerAmountPacket;
import de.syscy.kagecloud.network.packet.info.UpdatePingDataPacket;
import de.syscy.kagecloud.network.packet.node.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.node.RegisterProxyPacket;
import de.syscy.kagecloud.network.packet.node.ShutdownPacket;
import de.syscy.kagecloud.network.packet.player.ConnectPlayerIDPacket;
import de.syscy.kagecloud.network.packet.player.ConnectPlayerPacket;
import de.syscy.kagecloud.network.packet.player.ConnectedServerInfoPacket;
import de.syscy.kagecloud.network.packet.player.KickPlayerPacket;
import de.syscy.kagecloud.network.packet.player.MessagePacket;
import de.syscy.kagecloud.network.packet.proxy.AddServerPacket;
import de.syscy.kagecloud.network.packet.proxy.RemoveServerPacket;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ReflectionListener;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

@RequiredArgsConstructor
public class CloudProxyNetworkListener extends ReflectionListener {
	private final KageCloudBungee bungee;

	@Override
	public void connected(Connection connection) {
		connection.sendTCP(new RegisterProxyPacket(bungee.getNodeId(), bungee.getNodeName(), bungee.getCredentials()));
		connection.sendTCP(new ChangeStatusPacket(ServerStatus.RUNNING));
	}

	@Override
	public void disconnected(Connection connection) {
		BungeeCord.getInstance().stop("Shutting down network.");
	}

	public void received(Connection connection, ShutdownPacket packet) {
		BungeeCord.getInstance().stop(packet.getReason() != null ? packet.getReason() : "Shutting down network.");
	}

	public void received(Connection connection, AddServerPacket packet) {
		bungee.addServerInfo(connection, packet);
	}

	public void received(Connection connection, RemoveServerPacket packet) {
		bungee.removeServer(packet.getId());
	}

	public void received(Connection connection, KickPlayerPacket packet) {
		ProxiedPlayer player = BungeeCord.getInstance().getPlayer(UUID.fromString(packet.getPlayerId()));

		if(player != null) {
			player.disconnect(ComponentSerializer.parse(packet.getJsonReason()));
		}
	}

	public void received(Connection connection, MessagePacket packet) {
		ProxiedPlayer player = BungeeCord.getInstance().getPlayer(UUID.fromString(packet.getReceiverId()));

		if(player != null) {
			player.sendMessage(ChatMessageType.valueOf(packet.getType().name()), ComponentSerializer.parse(packet.getJsonMessage()));
		}
	}

	public void received(Connection connection, ConnectPlayerPacket packet) {
		ProxiedPlayer player = BungeeCord.getInstance().getPlayer(UUID.fromString(packet.getPlayerId()));
		Optional<ServerInfo> serverInfo = getServer(packet.getServerName(), packet.isExactServer());

		if(player != null && serverInfo.isPresent()) {
			player.connect(serverInfo.get());
		}
	}

	public void received(Connection connection, ConnectPlayerIDPacket packet) {
		ProxiedPlayer player = BungeeCord.getInstance().getPlayer(UUID.fromString(packet.getPlayerId()));
		Optional<ServerInfo> serverInfo = getServer(packet.getServerName(), packet.isExactServer());

		if(player != null && serverInfo.isPresent()) {
			player.connect(serverInfo.get());

			ConnectedServerInfoPacket response = new ConnectedServerInfoPacket(serverInfo.get().getName());
			response.setId(packet.getId());
			Packet finalResponse = response.buildResponse(packet);

			bungee.getClient().sendTCP(finalResponse);
		}
	}

	private Optional<ServerInfo> getServer(String serverName, boolean exactServer) {
		if(exactServer) {
			return Optional.ofNullable(BungeeCord.getInstance().getServerInfo(serverName));
		} else {
			Optional<CloudServerInfo> cloudServerInfoOptional = bungee.getServers().values().parallelStream().filter(s -> s.getTemplateName().equalsIgnoreCase(serverName)).findAny();
			return cloudServerInfoOptional.isPresent() ? Optional.of((ServerInfo) cloudServerInfoOptional.get()) : Optional.empty();
		}
	}

	public void received(Connection connection, PluginDataPacket packet) {
		bungee.onPluginData(connection, packet);
	}

	public void received(Connection connection, PlayerAmountPacket packet) {
		for(Entry<de.syscy.kagecloud.util.UUID, Integer> amountEntry : packet.getPlayerAmount().entrySet()) {
			CloudServerInfo serverInfo = bungee.getServers().get(amountEntry.getKey());

			if(serverInfo != null) {
				serverInfo.setActualPlayers(amountEntry.getValue());
			}
		}
	}

	public void received(Connection connection, UpdatePingDataPacket packet) {
		bungee.getPingListener().updateFromPacket(packet);
	}
}