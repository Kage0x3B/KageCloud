package de.syscy.kagecloud.event;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.plugin.Cancellable;
import de.syscy.kagecloud.plugin.Event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Called when a player connects to the network.
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class PlayerLoginEvent extends Event {
	/**
	 * Player connecting.
	 */
	private final CloudPlayer player;
}