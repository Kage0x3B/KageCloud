package de.syscy.kagecloud.network.packet.info;

import java.util.Map;

import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PlayerAmountPacket extends Packet {
	private @Getter Map<UUID, Integer> playerAmount;
}