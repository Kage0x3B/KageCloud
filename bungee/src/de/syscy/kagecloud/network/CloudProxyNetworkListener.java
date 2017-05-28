package de.syscy.kagecloud.network;

import java.util.UUID;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ReflectionListener;

import de.syscy.kagecloud.KageCloudBungee;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.packet.node.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.node.RegisterProxyPacket;
import de.syscy.kagecloud.network.packet.node.ShutdownPacket;
import de.syscy.kagecloud.network.packet.player.KickPlayerPacket;
import de.syscy.kagecloud.network.packet.player.MessagePacket;
import de.syscy.kagecloud.network.packet.proxy.AddServerPacket;
import de.syscy.kagecloud.network.packet.proxy.RemoveServerPacket;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatMessageType;
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
}