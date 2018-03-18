package de.syscy.kagecloud.webserver;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public interface IRequestHandler {
	public Response handle(String request, Session session, IHTTPSession httpSession);

	default public boolean needsAuthToken() {
		return true;
	}
}