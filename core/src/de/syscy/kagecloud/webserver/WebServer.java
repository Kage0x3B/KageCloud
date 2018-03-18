package de.syscy.kagecloud.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.util.UUID;
import de.syscy.kagecloud.webserver.backendhandler.AjaxButtonHandler;
import de.syscy.kagecloud.webserver.backendhandler.AuthHandler;
import de.syscy.kagecloud.webserver.backendhandler.PlayerActionHandler;
import de.syscy.kagecloud.webserver.backendhandler.PlayerDataTableHandler;
import de.syscy.kagecloud.webserver.backendhandler.ServerDataTableHandler;
import de.syscy.kagecloud.webserver.pages.DashboardPage;
import de.syscy.kagecloud.webserver.pages.PlayerPage;
import de.syscy.kagecloud.webserver.pages.PlayersPage;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {
	public static final long AUTH_TOKEN_TIME = 1000 * 60 * 60 * 24; //24 hours

	private Map<String, BackendRequestHandler> backendRequestHandlers = new HashMap<>();
	private Map<String, IRequestHandler> requestHandlers = new HashMap<>();

	private Map<String, Session> sessions = new HashMap<>();

	private Map<UUID, AuthToken> authTokens = new HashMap<>();

	public WebServer(KageCloudCore cloud) {
		super(7494);

		try {
			start(SOCKET_READ_TIMEOUT, false);
		} catch(IOException ex) {
			ex.printStackTrace();
		}

		AuthHandler authHandler = new AuthHandler(cloud, this);
		addBackendRequestHandler("auth", authHandler);
		addBackendRequestHandler("createtoken", authHandler);

		addRequestHandler("/", new DashboardPage(cloud));
		addRequestHandler("/players", new PlayersPage());
		addRequestHandler("/player", new PlayerPage(cloud));

		addBackendRequestHandler("ajaxButton", new AjaxButtonHandler());

		PlayerActionHandler playerActionHandler = new PlayerActionHandler(cloud);
		addBackendRequestHandler("kickPlayer", playerActionHandler);
		addBackendRequestHandler("messagePlayer", playerActionHandler);

		addBackendRequestHandler("playerDataTable", new PlayerDataTableHandler(cloud));
		addBackendRequestHandler("serverDataTable", new ServerDataTableHandler(cloud));
	}

	public void createAuthToken(String playerName, UUID authToken) {
		UUID existingTokenId = null;

		for(Entry<UUID, AuthToken> token : authTokens.entrySet()) {
			if(token.getValue().getPlayer().equals(playerName)) {
				existingTokenId = token.getKey();
			}
		}

		authTokens.remove(existingTokenId);

		authTokens.put(authToken, new AuthToken(playerName, System.currentTimeMillis()));
	}

	public void addRequestHandler(String request, IRequestHandler requestHandler) {
		request = request.substring(0, request.length() - (request.endsWith("/") ? 1 : 0));

		requestHandlers.put(request.toLowerCase(), requestHandler);
	}

	public void addBackendRequestHandler(String request, BackendRequestHandler backendRequestHandler) {
		backendRequestHandlers.put(request.toLowerCase(), backendRequestHandler);
	}

	@Override
	public Response serve(IHTTPSession httpSession) {
		Session session = getSession(httpSession);
		System.out.println("handling request " + httpSession.getUri());
		boolean authorized = /*false //DEBUG*/ true;

		UUID authTokenId = session.getAuthTokenId();
		AuthToken authToken = authTokens.get(authTokenId);

		if(authToken != null) {
			if(System.currentTimeMillis() < authToken.getCreationTime() + AUTH_TOKEN_TIME) {
				authorized = true;
			} else {
				authTokens.remove(authTokenId);
			}
		}

		String request = httpSession.getUri().toLowerCase();

		if(request.endsWith("/")) {
			request = request.substring(0, request.length() - 1);
		}

		if(request.startsWith("/backend/")) {
			request = request.substring(9);
			int slashIndex = request.indexOf('/');
			request = request.substring(0, slashIndex == -1 ? request.length() : slashIndex);

			BackendRequestHandler backendRequestHandler = backendRequestHandlers.get(request);

			if(backendRequestHandler != null) {
				if(backendRequestHandler.needsAuthToken() && !authorized) {
					return newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, "Unauthorized");
				}

				return backendRequestHandler.handle(request, session, httpSession);
			} else {
				return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Backend request type not supported");
			}
		} else {
			if(!authorized) {
				return newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, "Unauthorized");
			}

			String uri = httpSession.getUri();

			if(uri.endsWith("/")) {
				uri += "index.html";
			}

			if(requestHandlers.containsKey(request)) {
				return requestHandlers.get(request).handle(request, session, httpSession);
			} else {
				InputStream inputStream = WebServer.class.getResourceAsStream("/webinterface" + uri);

				if(inputStream == null) {
					return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
				}

				return newChunkedResponse(Response.Status.OK, NanoHTTPD.getMimeTypeForFile(uri), inputStream);
			}
		}
	}

	private Session getSession(IHTTPSession httpSession) {
		Session session;
		String sessionId = httpSession.getCookies().read("SID");

		if(sessionId == null) {
			sessionId = UUID.randomUUID().toString();
			session = new Session();
			sessions.put(sessionId, session);

			httpSession.getCookies().set("SID", sessionId, 1);
		} else {
			if(!sessions.containsKey(sessionId)) {
				session = new Session();
				sessions.put(sessionId, session);
			} else {
				session = sessions.get(sessionId);
			}
		}

		return session;
	}

	public static void main(String[] args) {
		new WebServer(null);
	}
}