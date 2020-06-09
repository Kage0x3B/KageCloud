package de.syscy.kagecloud.event;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.CloudServer;
import de.syscy.kagecloud.plugin.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Called when a player is connected to a server.
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class ServerConnectedEvent extends Event {
	/**
	 * Player connecting.
	 */
	private final CloudPlayer player;

	private final CloudServer server;
}