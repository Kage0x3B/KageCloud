package de.syscy.kagecloud.event;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.plugin.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Called when a player has left the network, it is not safe to call any methods
 * that perform an action on the passed player instance.
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class PlayerDisconnectEvent extends Event {
	/**
	 * Player disconnecting.
	 */
	private final CloudPlayer player;
}