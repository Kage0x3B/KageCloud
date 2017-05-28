package de.syscy.kagecloud.network.packet.node;

import de.syscy.kagecloud.network.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ShutdownPacket extends Packet {
	private @Getter String reason = null;
}