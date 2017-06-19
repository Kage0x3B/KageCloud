package de.syscy.kagecloud.plugin;

import org.w3c.dom.events.EventException;

public interface EventExecutor {
	public void execute(Listener listener, Event event) throws EventException;
}
