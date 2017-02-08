package de.syscy.kagecloud.event;

import de.syscy.kagecloud.network.CloudConnection;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

@RequiredArgsConstructor
public class ServerStatusChangeEvent extends Event {
	private final @Getter CloudConnection connection;
	private final @Getter ServerStatus status;
}