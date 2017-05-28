package de.syscy.kagecloud.event;

import de.syscy.kagecloud.CloudServerInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

@RequiredArgsConstructor
public class ServerStoppedEvent extends Event {
	private final @Getter CloudServerInfo serverInfo;
}