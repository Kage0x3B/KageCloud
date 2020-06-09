package de.syscy.kagecloud.event;

import de.syscy.kagecloud.CloudServerInfo;
import lombok.Data;
import lombok.Getter;

@Data
public class ServerStoppedEvent {
	private final @Getter CloudServerInfo serverInfo;
}