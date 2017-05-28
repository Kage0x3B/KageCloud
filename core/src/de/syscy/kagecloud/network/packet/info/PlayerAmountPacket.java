package de.syscy.kagecloud.network.packet.info;

import de.syscy.kagecloud.network.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PlayerAmountPacket extends Packet {
	private @Getter int playerAmount;
}