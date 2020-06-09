package de.syscy.kagecloud.network.packet.player;

import de.syscy.kagecloud.network.packet.Packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PlayerLeaveNetworkPacket extends Packet {
	private @Getter String id;
}