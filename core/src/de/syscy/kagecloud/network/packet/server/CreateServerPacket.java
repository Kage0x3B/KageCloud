package de.syscy.kagecloud.network.packet.server;

import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class CreateServerPacket extends Packet {
	private @Getter UUID serverId;
	private @Getter String templateName;
	private @Getter String serverName;

	private @Getter Map<String, String> extraData;
}