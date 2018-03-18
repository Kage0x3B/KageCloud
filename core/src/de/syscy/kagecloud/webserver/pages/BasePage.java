package de.syscy.kagecloud.webserver.pages;

import java.util.List;

import de.syscy.kagecloud.webserver.IRequestHandler;
import de.syscy.kagecloud.webserver.Session;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.RootPageContainer;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public abstract class BasePage implements IRequestHandler {
	@Override
	public Response handle(String request, Session session, IHTTPSession httpSession) {
		String page = constructPage(new RootPageContainer(session, isEmpty()), request, session, httpSession).toString();

		if(page != null) {
			return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_HTML, page);
		} else {
			return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "Internal error: Empty page");
		}
	}

	public boolean isEmpty() {
		return false;
	}

	public abstract RootPageContainer constructPage(RootPageContainer page, String request, Session session, IHTTPSession httpSession);

	protected String buildRedirectPage(String redirectURL) {
		return buildRedirectPage(redirectURL, 0, "");
	}

	protected String buildRedirectPage(String redirectURL, int duration, String message) {
		if(duration <= 0) {
			return "<!DOCTYPE html><html><head><meta http-equiv=\"refresh\" content=\"0;url=" + redirectURL + "\"><title>SyscyConnect</title><script language=\"javascript\">window.location.href = \"" + redirectURL + "\"</script></head><body>" + message + "<br><br><a href=\"" + redirectURL + "\">Click here if you don't get redirected.</a></body></html>";
		} else {
			return "<!DOCTYPE html><html><head><meta http-equiv=\"refresh\" content=\"" + duration + ";url=" + redirectURL + "\"><title>SyscyConnect</title><script language=\"javascript\">setTimeout(function() {window.location.href = \"" + redirectURL + "\"}, " + duration * 1000 + ");</script></head><body>" + message + "<br><br><a href=\"" + redirectURL + "\">Click here if you don't get redirected in " + duration + " seconds.</a></body></html>";
		}
	}

	protected String getParameter(IHTTPSession session, String parameter) {
		List<String> values = session.getParameters().get(parameter);

		if(values.size() > 0) {
			return values.get(0);
		} else {
			return "";
		}
	}
}