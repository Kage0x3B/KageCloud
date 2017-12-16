package de.syscy.kagecloud.network.packet.player;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.util.ChatMessageType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class MessagePacket extends Packet {
	private @Getter String receiverId;
	private @Getter String jsonMessage;
	private @Getter ChatMessageType type = ChatMessageType.CHAT;

	public MessagePacket(CloudPlayer receiver, String jsonMessage) {
		receiverId = receiver.getId().toString();
		this.jsonMessage = jsonMessage;
	}

	public MessagePacket(CloudPlayer receiver, String jsonMessage, ChatMessageType type) {
		receiverId = receiver.getId().toString();
		this.jsonMessage = jsonMessage;
		this.type = type;
	}
}