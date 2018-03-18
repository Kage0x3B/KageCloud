package de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons;

import de.syscy.kagecloud.webserver.Session;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import lombok.Getter;

public abstract class ButtonClickListener {
	private final @Getter long creationTime = System.currentTimeMillis();

	public abstract void onButtonClick(String buttonId, Session session, IHTTPSession httpSession);
}