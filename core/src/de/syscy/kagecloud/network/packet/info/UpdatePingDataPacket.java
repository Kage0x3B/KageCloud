package de.syscy.kagecloud.network.packet.info;

import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.util.ServerPingData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class UpdatePingDataPacket extends Packet {
	private @Getter @Setter String supportedProtocolString = null;
	private @Getter @Setter int minProtocolVersion = -1;
	private @Getter @Setter int maxProtocolVersion = -1;

	private @Getter @Setter int maxPlayers = -1;
	private @Getter @Setter int onlinePlayers = -1;
	private @Getter @Setter String[] playerInfoHover = null;

	private @Getter @Setter String motd = null;

	private @Getter @Setter String faviconData = null;

	public UpdatePingDataPacket(ServerPingData pingData) {
		pingData.updateData();

		supportedProtocolString = pingData.getSupportedProtocolString();
		minProtocolVersion = pingData.getMinProtocolVersion();
		maxProtocolVersion = pingData.getMaxProtocolVersion();

		maxPlayers = pingData.getMaxPlayers();
		playerInfoHover = pingData.getPlayerInfoHover();

		motd = pingData.getMotd();

		faviconData = pingData.getFaviconData();
	}
}