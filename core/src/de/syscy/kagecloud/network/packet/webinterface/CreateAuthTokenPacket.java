package de.syscy.kagecloud.network.packet.webinterface;

import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CreateAuthTokenPacket extends Packet {
	private @Getter String playerName;
	private @Getter UUID authToken;
}