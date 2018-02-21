package de.syscy.kagecloud;

import java.net.InetSocketAddress;

import de.syscy.kagecloud.util.UUID;

import com.esotericsoftware.kryonet.Connection;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.BungeeServerInfo;

public class CloudServerInfo extends BungeeServerInfo implements Comparable<CloudServerInfo> {
	private final @Getter UUID id;
	private final @Getter String templateName;
	private final @Getter boolean lobby;

	private @Getter @Setter int actualPlayers;

	protected CloudServerInfo(Connection connection, UUID id, String name, InetSocketAddress address, String motd, boolean restricted, String templateName, boolean lobby) {
		super(name, address, motd, restricted);

		this.id = id;
		this.templateName = templateName;
		this.lobby = lobby;
	}

	@Override
	public int compareTo(CloudServerInfo serverInfo) {
		return actualPlayers - serverInfo.getActualPlayers();
	}
}