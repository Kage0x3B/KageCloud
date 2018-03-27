package de.syscy.kagecloud.network.packet.player;

import de.syscy.kagecloud.network.packet.Packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PlayerJoinNetworkPacket extends Packet {
	private @Getter String id;
	private @Getter String name;
	private @Getter int version;

	private @Getter String loginId;
}