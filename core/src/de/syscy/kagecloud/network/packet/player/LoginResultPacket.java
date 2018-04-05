package de.syscy.kagecloud.network.packet.player;

import de.syscy.kagecloud.network.packet.Packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@AllArgsConstructor
public class LoginResultPacket extends Packet {
	private @Getter String loginId;
	private @Getter Result result;
	private @Getter String message;

	/**
	 *
	 * @param loginId The loginId received in the {@link PlayerLateJoinServerPacket}
	 */
	public LoginResultPacket(@NonNull String loginId) {
		this(loginId, Result.ALLOWED, null);
	}

	public static enum Result {
		ALLOWED, DISALLOWED;
	}
}