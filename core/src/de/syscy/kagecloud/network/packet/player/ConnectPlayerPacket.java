package de.syscy.kagecloud.network.packet.player;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.CloudServer;
import de.syscy.kagecloud.network.packet.Packet;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ConnectPlayerPacket extends Packet {
	private @Getter String playerId;
	private @Getter String serverName;
	private @Getter boolean exactServer;

	public ConnectPlayerPacket(CloudPlayer player, CloudServer server) {
		this(player.getId().toString(), server.getName(), true);
	}

	public ConnectPlayerPacket(String playerId, String serverName) {
		this(playerId, serverName, true);
	}

	/**
	 * @param playerId The UUID of the player as a String
	 * @param serverName The name of the server or a template name if exactServer is set to false
	 * @param exactServer Set to false to connect the player to an available server with the template name
	 */
	public ConnectPlayerPacket(String playerId, String serverName, boolean exactServer) {
		this.playerId = playerId;
		this.serverName = serverName;
		this.exactServer = exactServer;
	}
}