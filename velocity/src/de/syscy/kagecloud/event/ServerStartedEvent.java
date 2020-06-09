package de.syscy.kagecloud.event;

import de.syscy.kagecloud.CloudServerInfo;
import lombok.Data;

@Data
public final class ServerStartedEvent {
	private final CloudServerInfo serverInfo;
}