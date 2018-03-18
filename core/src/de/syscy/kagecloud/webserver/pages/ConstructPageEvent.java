package de.syscy.kagecloud.webserver.pages;

import de.syscy.kagecloud.plugin.Event;
import de.syscy.kagecloud.webserver.Session;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.RootPageContainer;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ConstructPageEvent extends Event {
	private final RootPageContainer page;
	private final String request;
	private final Session session;
	private final IHTTPSession httpSession;
}