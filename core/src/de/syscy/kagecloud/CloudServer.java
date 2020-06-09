package de.syscy.kagecloud;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import de.syscy.kagecloud.network.CloudConnection;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.IConnectionRepresentation;
import de.syscy.kagecloud.util.UUID;

import lombok.Getter;

public class CloudServer implements IConnectionRepresentation, Comparable<CloudServer> {
	private final @Getter CloudConnection connection;

	private final @Getter String templateName;
	private final @Getter boolean lobby;

	private final @Getter String name;
	private final @Getter InetSocketAddress address;
	private final @Getter Map<UUID, CloudPlayer> players = new HashMap<>();
	private final @Getter boolean restricted;

	public CloudServer(CloudConnection connection, String name, InetSocketAddress address, boolean restricted, String templateName, boolean lobby) { //TODO Should be protected
		this.connection = connection;

		this.templateName = templateName;
		this.lobby = lobby;

		this.name = name;
		this.address = address;
		this.restricted = restricted;
	}

	public boolean canAccess(CloudPlayer player) {
		return connection != null && connection.getServerStatus() == ServerStatus.RUNNING;
	}

	@Override
	public int compareTo(CloudServer serverInfo) {
		return players.size() - serverInfo.getPlayers().size();
	}

	public static boolean isNotEmptyLobby(CloudServer cloudServer) {
		return cloudServer.isLobby() && cloudServer.getPlayers().size() > 0;
	}
}