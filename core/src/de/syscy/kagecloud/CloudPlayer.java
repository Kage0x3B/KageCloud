package de.syscy.kagecloud;

import de.syscy.kagecloud.chat.BaseComponent;
import de.syscy.kagecloud.chat.ComponentSerializer;
import de.syscy.kagecloud.chat.TextComponent;
import de.syscy.kagecloud.network.CloudCoreConnection;
import de.syscy.kagecloud.network.packet.player.KickPlayerPacket;
import de.syscy.kagecloud.network.packet.player.MessagePacket;
import de.syscy.kagecloud.util.ChatMessageType;
import de.syscy.kagecloud.util.ProtocolConstants;
import de.syscy.kagecloud.util.UUID;
import lombok.Data;

@Data
public class CloudPlayer implements CommandSender {
	private final UUID id;
	private final String name;
	private final int version;

	private final CloudCoreConnection bungeeCordProxy;
	private CloudServer currentServer;

	/**
	 * Connects / transfers this user to the specified connection, gracefully
	 * closing the current one. Depending on the implementation, this method
	 * might return before the user has been connected.
	 *
	 * @param target the new server to connect to
	 */
	public void connect(CloudServer target) {

	}

	/**
	 * Kicks the player from the network
	 *
	 * @param reason the reason shown to the player
	 */
	public void kick(String reason) {
		kick(TextComponent.fromLegacyText(reason));
	}

	/**
	 * Kicks the player from the network
	 *
	 * @param reason the reason shown to the player
	 */
	public void kick(BaseComponent... reason) {
		bungeeCordProxy.sendTCP(new KickPlayerPacket(this, ComponentSerializer.toString(reason)));
	}

	/**
	 * Kicks the player from the network
	 *
	 * @param reason the reason shown to the player
	 */
	public void kick(BaseComponent reason) {
		bungeeCordProxy.sendTCP(new KickPlayerPacket(this, ComponentSerializer.toString(reason)));
	}

	@Override
	public void sendMessage(String message) {
		sendMessage(TextComponent.fromLegacyText(message));
	}

	@Override
	public void sendMessages(String... messages) {
		for(String message : messages) {
			sendMessage(message);
		}
	}

	@Override
	public void sendMessage(BaseComponent message) {
		sendMessage(ChatMessageType.CHAT, message);
	}

	@Override
	public void sendMessage(BaseComponent... message) {
		sendMessage(ChatMessageType.CHAT, message);
	}

	/**
	 * Send a message to the specified screen position of this player.
	 *
	 * @param position the screen position
	 * @param message the message to send
	 */
	public void sendMessage(ChatMessageType position, BaseComponent... message) {
		// Action bar on 1.8 doesn't display the new JSON formattings, legacy works - send it using this for now
		if(position == ChatMessageType.ACTION_BAR && version <= ProtocolConstants.MINECRAFT_1_8) {
			sendMessage(position, ComponentSerializer.toString(new TextComponent(BaseComponent.toLegacyText(message))));
		} else {
			sendMessage(position, ComponentSerializer.toString(message));
		}
	}

	/**
	 * Send a message to the specified screen position of this player.
	 *
	 * @param position the screen position
	 * @param message the message to send
	 */
	public void sendMessage(ChatMessageType position, BaseComponent message) {
		// Action bar on 1.8 doesn't display the new JSON formattings, legacy works - send it using this for now
		if(position == ChatMessageType.ACTION_BAR && version <= ProtocolConstants.MINECRAFT_1_8) {
			sendMessage(position, ComponentSerializer.toString(new TextComponent(BaseComponent.toLegacyText(message))));
		} else {
			sendMessage(position, ComponentSerializer.toString(message));
		}
	}

	private void sendMessage(ChatMessageType position, String jsonMessage) {
		bungeeCordProxy.sendTCP(new MessagePacket(this, jsonMessage, position));
	}

	@Override
	public boolean isOp() {
		return false; //TODO
	}
}