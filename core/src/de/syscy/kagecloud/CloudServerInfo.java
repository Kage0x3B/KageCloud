package de.syscy.kagecloud;

import java.net.InetSocketAddress;

import de.syscy.kagecloud.network.CloudConnection;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import lombok.Getter;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.CommandSender;

public class CloudServerInfo extends BungeeServerInfo implements Comparable<CloudServerInfo> {
	private final @Getter CloudConnection connection;

	private final @Getter String templateName;
	private final @Getter boolean lobby;

	protected CloudServerInfo(CloudConnection connection, String name, InetSocketAddress address, String motd, boolean restricted, String templateName, boolean lobby) {
		super(name, address, motd, restricted);

		this.connection = connection;

		this.templateName = templateName;
		this.lobby = lobby;
	}

	@Override
	public boolean canAccess(CommandSender player) {
		return connection != null && connection.getServerStatus() == ServerStatus.RUNNING && super.canAccess(player);
	}

	@Override
	public int compareTo(CloudServerInfo serverInfo) {
		return getPlayers().size() - serverInfo.getPlayers().size();
	}
}