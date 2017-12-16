package de.syscy.kagecloud.network.packet.player;

import de.syscy.kagecloud.network.packet.IDPacket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ConnectedServerInfoPacket extends IDPacket {
	private @Getter String serverName;
}