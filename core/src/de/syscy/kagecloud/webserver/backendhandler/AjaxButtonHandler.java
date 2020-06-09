package de.syscy.kagecloud.webserver.backendhandler;

import de.syscy.kagecloud.webserver.BackendRequestHandler;
import de.syscy.kagecloud.webserver.Session;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons.ButtonClickListener;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AjaxButtonHandler extends BackendRequestHandler {
	@Override
	public Response handle(String request, Session session, IHTTPSession httpSession) {
		String buttonId = getParameter(httpSession, "id");

		if(buttonId != null && !buttonId.isEmpty()) {
			ButtonClickListener clickListener = session.getClickListeners().get(buttonId);

			if(clickListener != null) {
				clickListener.onButtonClick(buttonId, session, httpSession);
			}

			return newFixedLengthResponse("");
		} else {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Bad request");
		}
	}

	@Override
	public boolean needsAuthToken() {
		return true;
	}
}