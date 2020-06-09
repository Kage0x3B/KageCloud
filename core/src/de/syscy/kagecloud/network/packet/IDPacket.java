package de.syscy.kagecloud.network.packet;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class IDPacket extends Packet {
	private @Getter @Setter UUID id;
	private @Getter @Setter UUID senderId;

	public UUID prepareIDPacket(UUID senderId) {
		this.senderId = senderId;

		return id = UUID.randomUUID();
	}

	public Packet buildResponse(IDPacket originalPacket) {
		if(KageCloud.cloudNode.getNodeName().equals("core")) {
			return this;
		} else {
			return RelayPacket.toNode(originalPacket.senderId, this);
		}
	}
}