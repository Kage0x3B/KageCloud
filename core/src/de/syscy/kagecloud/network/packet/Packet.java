package de.syscy.kagecloud.network.packet;

import com.esotericsoftware.kryo.Kryo;

import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.packet.info.PlayerAmountPacket;
import de.syscy.kagecloud.network.packet.node.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.node.RegisterProxyPacket;
import de.syscy.kagecloud.network.packet.node.RegisterServerPacket;
import de.syscy.kagecloud.network.packet.node.RegisterWrapperPacket;
import de.syscy.kagecloud.network.packet.node.ShutdownPacket;
import de.syscy.kagecloud.network.packet.player.KickPlayerPacket;
import de.syscy.kagecloud.network.packet.player.MessagePacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinServerPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveServerPacket;
import de.syscy.kagecloud.network.packet.proxy.AddServerPacket;
import de.syscy.kagecloud.network.packet.proxy.RemoveServerPacket;
import de.syscy.kagecloud.network.packet.webinterface.CreateAuthTokenPacket;
import de.syscy.kagecloud.util.ChatMessageType;
import de.syscy.kagecloud.util.UUID;

public class Packet {
	public static void registerKryoClasses(Kryo kryo) {
		kryo.register(CreateServerPacket.class);
		kryo.register(ExecuteCommandPacket.class);

		kryo.register(RegisterProxyPacket.class);
		kryo.register(RegisterWrapperPacket.class);
		kryo.register(RegisterServerPacket.class);
		kryo.register(ChangeStatusPacket.class);
		kryo.register(ShutdownPacket.class);

		kryo.register(PlayerJoinNetworkPacket.class);
		kryo.register(PlayerJoinServerPacket.class);
		kryo.register(PlayerLeaveNetworkPacket.class);
		kryo.register(PlayerLeaveServerPacket.class);
		kryo.register(MessagePacket.class);
		kryo.register(KickPlayerPacket.class);

		kryo.register(AddServerPacket.class);
		kryo.register(RemoveServerPacket.class);

		kryo.register(PlayerAmountPacket.class);

		kryo.register(CreateAuthTokenPacket.class);

		kryo.register(UUID.class, new UUID.UUIDSerializer());
		kryo.register(ServerStatus.class);
		kryo.register(ChatMessageType.class);
	}
}