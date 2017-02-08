package de.syscy.kagecloud.network.packet;

import de.syscy.kagecloud.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RegisterServerPacket extends Packet {
	private @Getter UUID id;
	private @Getter String name;
	private @Getter String credentials;
	private @Getter int port;

	private @Getter String templateName;
	private @Getter boolean lobby;
}