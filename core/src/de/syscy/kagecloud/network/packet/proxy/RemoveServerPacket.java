package de.syscy.kagecloud.network.packet.proxy;

import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RemoveServerPacket extends Packet {
	private @Getter UUID id;
}