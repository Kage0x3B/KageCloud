package de.syscy.kagecloud.network.packet;

import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusPacket extends Packet {
	private @Getter ServerStatus status;
}