package de.syscy.kagecloud.webserver.backendhandler;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.util.UUID;
import de.syscy.kagecloud.webserver.BackendRequestHandler;
import de.syscy.kagecloud.webserver.Session;
import de.syscy.kagecloud.webserver.WebServer;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthHandler extends BackendRequestHandler {
	private final KageCloudCore cloud;
	private final WebServer webServer;

	@Override
	public Response handle(String request, Session session, IHTTPSession httpSession) {
		String address = cloud.getConfig().getString("webinterfaceAddress", "http://localhost:7494");

		String redirectResponse = "<html><head>";
		redirectResponse += "<meta http-equiv=\"refresh\" content=\"1; url=" + address + "/\" />";
		redirectResponse += "</head><body>";
		redirectResponse += "Authenticated successfully. <a href=\"" + address + "/\">Click here to get to the webinterface.</a>";
		redirectResponse += "</body></html>";

		switch(request) {
			case "auth":
				String authTokenString = getParameter(httpSession, "at");

				UUID authTokenId = UUID.fromString(authTokenString);

				if(authTokenId != null) {
					session.setAuthTokenId(authTokenId);

					return newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_HTML, redirectResponse);
				} else {
					return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Bad request");
				}
			case "createtoken":
				String secret = getParameter(httpSession, "s");

				if(secret.isEmpty()) {
					return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Bad request");
				}

				if(secret.equals(getSecret())) {
					authTokenId = UUID.randomUUID();

					webServer.createAuthToken("op", authTokenId);

					session.setAuthTokenId(authTokenId);

					return newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_HTML, redirectResponse);
				} else {
					return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Bad request");
				}
		}

		return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Bad request");
	}

	@Override
	public boolean needsAuthToken() {
		return false;
	}

	private String getSecret() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		String salt = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		String input = KageCloud.cloudNode.getCredentials() + salt;

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(input.getBytes(Charset.defaultCharset()));
			byte[] hash = digest.digest();

			return new String(hash, Charset.defaultCharset());
		} catch(NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}

		return "";
	}
}