package de.syscy.kagecloud.network.packet.info.gui;

import java.util.List;

import de.syscy.kagecloud.CloudServer;
import de.syscy.kagecloud.network.packet.IDPacket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ServerListPacket extends IDPacket {
	private @Getter List<Server> server;

	@Data
	public static class Server {
		private String templateName;
		private boolean lobby;

		private String name;
		private boolean restricted;

		private Server() {

		}

		@Override
		public String toString() {
			return name;
		}

		public static Server fromCloudServer(CloudServer cloudServer) {
			Server server = new Server();

			server.templateName = cloudServer.getTemplateName();
			server.lobby = cloudServer.isLobby();
			server.name = cloudServer.getName();
			server.restricted = cloudServer.isRestricted();

			return server;
		}
	}
}