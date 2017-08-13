package de.syscy.kagecloud.network.packet.player;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.CloudServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ConnectPlayerPacket {
	private @Getter String playerId;
	private @Getter String serverName;

	public ConnectPlayerPacket(CloudPlayer player, CloudServer server) {
		playerId = player.getId().toString();
		serverName = server.getName();
	}
}