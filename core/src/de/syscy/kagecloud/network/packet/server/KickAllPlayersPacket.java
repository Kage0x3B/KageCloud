package de.syscy.kagecloud.network.packet.server;

import de.syscy.kagecloud.network.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class KickAllPlayersPacket extends Packet {
	private @Getter String serverName;
}