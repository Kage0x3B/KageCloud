package de.syscy.kagecloud.network.packet.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PlayerLeaveServerPacket {
	private @Getter String playerId;
	private @Getter String serverId;
}