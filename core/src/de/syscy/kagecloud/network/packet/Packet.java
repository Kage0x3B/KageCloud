package de.syscy.kagecloud.network.packet;

import com.esotericsoftware.kryo.Kryo;

import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.util.UUID;

public class Packet {
	public static void registerClasses(Kryo kryo) {
		kryo.register(RegisterWrapperPacket.class);
		kryo.register(RegisterServerPacket.class);
		kryo.register(CreateServerPacket.class);
		kryo.register(ChangeStatusPacket.class);
		kryo.register(ExecuteCommandPacket.class);
		kryo.register(ShutdownPacket.class);

		kryo.register(UUID.class);
		kryo.register(ServerStatus.class);
	}
}