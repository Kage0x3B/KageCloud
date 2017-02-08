package de.syscy.kagecloud.network.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCommandPacket extends Packet {
	private @Getter String command;
}