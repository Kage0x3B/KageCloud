package de.syscy.kagecloud.network.packet.player;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.network.packet.Packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class KickPlayerPacket extends Packet {
	private @Getter String playerId;
	private @Getter String jsonReason;

	public KickPlayerPacket(CloudPlayer player, String jsonReason) {
		playerId = player.getId().toString();
		this.jsonReason = jsonReason;
	}
}