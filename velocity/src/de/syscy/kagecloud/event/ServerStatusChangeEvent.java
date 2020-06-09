package de.syscy.kagecloud.event;

import de.syscy.kagecloud.network.CloudConnection;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import lombok.Data;

@Data
public final class ServerStatusChangeEvent {
	private final CloudConnection connection;
	private final ServerStatus status;
}