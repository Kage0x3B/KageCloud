package de.syscy.kagecloud.network.packet.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PlayerLeaveNetworkPacket {
	private @Getter String id;
}