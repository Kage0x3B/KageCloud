package de.syscy.kagecloud.network.packet;

import java.util.ArrayList;
import java.util.HashMap;

import de.syscy.kagecloud.CloudServer;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.CloudConnection.Type;
import de.syscy.kagecloud.network.packet.info.PlayerAmountPacket;
import de.syscy.kagecloud.network.packet.info.UpdatePingDataPacket;
import de.syscy.kagecloud.network.packet.info.gui.PlayerListPacket;
import de.syscy.kagecloud.network.packet.info.gui.RequestPlayerListPacket;
import de.syscy.kagecloud.network.packet.info.gui.RequestServerListPacket;
import de.syscy.kagecloud.network.packet.info.gui.ServerListPacket;
import de.syscy.kagecloud.network.packet.node.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.node.RegisterProxyPacket;
import de.syscy.kagecloud.network.packet.node.RegisterServerPacket;
import de.syscy.kagecloud.network.packet.node.RegisterWrapperPacket;
import de.syscy.kagecloud.network.packet.node.ShutdownPacket;
import de.syscy.kagecloud.network.packet.player.ConnectPlayerIDPacket;
import de.syscy.kagecloud.network.packet.player.ConnectPlayerPacket;
import de.syscy.kagecloud.network.packet.player.ConnectedServerInfoPacket;
import de.syscy.kagecloud.network.packet.player.KickPlayerPacket;
import de.syscy.kagecloud.network.packet.player.LoginResultPacket;
import de.syscy.kagecloud.network.packet.player.MessagePacket;
import de.syscy.kagecloud.network.packet.player.PlayerEarlyJoinServerPacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLateJoinServerPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveServerPacket;
import de.syscy.kagecloud.network.packet.proxy.AddServerPacket;
import de.syscy.kagecloud.network.packet.proxy.RemoveServerPacket;
import de.syscy.kagecloud.network.packet.server.CreateServerPacket;
import de.syscy.kagecloud.network.packet.server.ExecuteCommandPacket;
import de.syscy.kagecloud.network.packet.server.KickAllPlayersPacket;
import de.syscy.kagecloud.network.packet.server.ReloadServerPacket;
import de.syscy.kagecloud.util.ChatMessageType;
import de.syscy.kagecloud.util.UUID;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;

public class Packet {
	public void sendTo(Connection connection) {
		connection.sendTCP(this);
	}

	public void sendTo(CloudServer server) {
		server.getConnection().sendTCP(this);
	}

	public static void registerKryoClasses(Kryo kryo) {
		kryo.register(CreateServerPacket.class);
		kryo.register(ExecuteCommandPacket.class);

		kryo.register(RegisterProxyPacket.class);
		kryo.register(RegisterWrapperPacket.class);
		kryo.register(RegisterServerPacket.class);
		kryo.register(ChangeStatusPacket.class);
		kryo.register(ShutdownPacket.class);
		kryo.register(ReloadServerPacket.class);

		kryo.register(PlayerJoinNetworkPacket.class);
		kryo.register(LoginResultPacket.class);
		kryo.register(PlayerEarlyJoinServerPacket.class);
		kryo.register(PlayerLateJoinServerPacket.class);
		kryo.register(PlayerLeaveNetworkPacket.class);
		kryo.register(PlayerLeaveServerPacket.class);
		kryo.register(ConnectPlayerPacket.class);
		kryo.register(ConnectPlayerIDPacket.class);
		kryo.register(ConnectedServerInfoPacket.class);
		kryo.register(MessagePacket.class);
		kryo.register(KickPlayerPacket.class);

		kryo.register(AddServerPacket.class);
		kryo.register(RemoveServerPacket.class);

		kryo.register(PlayerAmountPacket.class);
		kryo.register(UpdatePingDataPacket.class);
		kryo.register(RequestServerListPacket.class);
		kryo.register(ServerListPacket.class);
		kryo.register(RequestPlayerListPacket.class);
		kryo.register(PlayerListPacket.class);
		kryo.register(KickAllPlayersPacket.class);

		kryo.register(RelayPacket.class);
		kryo.register(PluginDataPacket.class, new PluginDataPacket.PluginDataSerializer());
		kryo.register(ChunkedPacket.class, new ChunkedPacket.ChunkedPacketSerializer());

		kryo.register(String[].class);
		kryo.register(UUID.class, new UUID.UUIDSerializer());
		kryo.register(ServerStatus.class);
		kryo.register(ChatMessageType.class);
		kryo.register(Type.class);
		kryo.register(LoginResultPacket.Result.class);
		kryo.register(HashMap.class);
		kryo.register(ArrayList.class);

		kryo.register(ServerListPacket.Server.class);
		kryo.register(PlayerListPacket.Player.class);
	}
}