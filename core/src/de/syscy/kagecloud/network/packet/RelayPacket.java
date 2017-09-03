package de.syscy.kagecloud.network.packet;

import de.syscy.kagecloud.network.CloudConnection.Type;
import de.syscy.kagecloud.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RelayPacket extends Packet {
	private @Getter Type receiverType;
	private @Getter UUID receiverId;
	private @Getter String receiverName;
	private @Getter boolean toAllOfType = false;

	private @Getter Packet packet;

	public static RelayPacket toAllOfType(Type type, Packet packet) {
		return new RelayPacket(type, null, null, true, packet);
	}

	public static RelayPacket toProxy(UUID id, Packet packet) {
		return new RelayPacket(Type.PROXY, id, null, false, packet);
	}

	public static RelayPacket toProxy(String name, Packet packet) {
		return new RelayPacket(Type.PROXY, null, name, false, packet);
	}

	public static RelayPacket toWrapper(UUID id, Packet packet) {
		return new RelayPacket(Type.WRAPPER, id, null, false, packet);
	}

	public static RelayPacket toWrapper(String name, Packet packet) {
		return new RelayPacket(Type.WRAPPER, null, name, false, packet);
	}

	public static RelayPacket toServer(UUID id, Packet packet) {
		return new RelayPacket(Type.SERVER, id, null, false, packet);
	}

	public static RelayPacket toServer(String name, Packet packet) {
		return new RelayPacket(Type.SERVER, null, name, false, packet);
	}
}