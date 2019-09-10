package de.syscy.kagecloud;

import com.esotericsoftware.kryonet.Connection;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.syscy.kagecloud.util.UUID;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

public class CloudServerInfo implements Comparable<CloudServerInfo> {
	private final @Getter UUID id;
	private final @Getter String templateName;
	private final @Getter boolean lobby;

	private final @Getter String name;
	private final @Getter InetSocketAddress address;
	private final @Getter ServerInfo velocityServerInfo;

	private @Getter @Setter int actualPlayers;

	protected CloudServerInfo(Connection connection, UUID id, String name, InetSocketAddress address, String templateName, boolean lobby) {
		this.id = id;
		this.templateName = templateName;
		this.lobby = lobby;

		this.name = name;
		this.address = address;
		this.velocityServerInfo = new ServerInfo(name, address);
	}

	@Override
	public int compareTo(CloudServerInfo serverInfo) {
		return actualPlayers - serverInfo.getActualPlayers();
	}
}