package de.syscy.kagecloud.network.packet.info.gui;

import java.util.List;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.network.packet.IDPacket;
import de.syscy.kagecloud.network.packet.info.gui.ServerListPacket.Server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PlayerListPacket extends IDPacket {
	private @Getter List<Player> players;

	@Data
	public static class Player {
		private String playerName;
		private String rankName;

		private String proxyName;
		private Server currentServer;

		private Player() {

		}

		@Override
		public String toString() {
			return playerName;
		}

		public static Player fromCloudPlayer(CloudPlayer cloudPlayer) {
			Player player = new Player();

			player.playerName = cloudPlayer.getName();
			player.rankName = "Not implemented"; //TODO: Implement ranks

			player.proxyName = cloudPlayer.getBungeeCordProxy().getName();
			player.currentServer = Server.fromCloudServer(cloudPlayer.getCurrentServer());

			return player;
		}
	}
}