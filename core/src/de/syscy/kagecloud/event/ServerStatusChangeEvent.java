package de.syscy.kagecloud.event;

import de.syscy.kagecloud.network.CloudConnection;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.plugin.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class ServerStatusChangeEvent extends Event {
	private final @Getter CloudConnection connection;
	private final @Getter ServerStatus status;
}