package de.syscy.kagecloud.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

@RequiredArgsConstructor
public class ServerCreateEvent extends Event implements Cancellable {
	private @Getter @Setter boolean cancelled;

	private final @Getter String templateName;
}