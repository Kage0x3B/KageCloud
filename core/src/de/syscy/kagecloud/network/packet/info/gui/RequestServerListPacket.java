package de.syscy.kagecloud.network.packet.info.gui;

import de.syscy.kagecloud.network.packet.IDPacket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RequestServerListPacket extends IDPacket {
	private @Getter String searchQuery = "";
}