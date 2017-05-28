package de.syscy.kagecloud.network.packet.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PlayerJoinNetworkPacket {
	private @Getter String id;
	private @Getter String name;
	private @Getter int version;
}