package de.syscy.kagecloud.util;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.CloudServer;
import de.syscy.kagecloud.network.CloudConnection;
import de.syscy.kagecloud.network.CloudCoreConnection;
import lombok.Data;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

public class StartingServerData extends CloudServer {
	private final @Getter UUID id;
	private final @Getter Map<String, String> extraData;

	public StartingServerData(UUID id, String templateName, Map<String, String> extraData) {
		super(createConnection(id, templateName), templateName, null, false, templateName, templateName.toLowerCase().contains("lobby"));
		this.id = id;
		this.extraData = extraData;
	}

	@Override
	public Map<UUID, CloudPlayer> getPlayers() {
		return Collections.emptyMap();
	}

	private static CloudConnection createConnection(UUID id, String templateName) {
		CloudCoreConnection connection = new CloudCoreConnection();
		connection.setNodeId(id);
		connection.setName("STARTING_" + templateName);

		return connection;
	}
}