package de.syscy.kagecloud.network.packet.node;

import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RegisterWrapperPacket extends Packet {
	private @Getter UUID id;
	private @Getter String name;
	private @Getter String credentials;
}