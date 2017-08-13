package de.syscy.kagecloud.network.packet.info;

import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.util.UUID;
import lombok.Getter;
import lombok.Setter;

public class IDPacket extends Packet {
	private @Getter @Setter UUID id;

	public UUID generateID() {
		return id = UUID.randomUUID();
	}
}