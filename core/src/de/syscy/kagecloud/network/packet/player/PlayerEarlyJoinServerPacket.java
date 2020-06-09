package de.syscy.kagecloud.network.packet.player;

import de.syscy.kagecloud.network.packet.Packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PlayerEarlyJoinServerPacket extends Packet {
	private @Getter String playerId;
	private @Getter String serverId;
}