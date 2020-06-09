package de.syscy.kagecloud.webserver.backendhandler;

import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.webserver.BackendRequestHandler;
import de.syscy.kagecloud.webserver.Session;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StatisticsHandler extends BackendRequestHandler {
	private final KageCloudCore cloud;

	@Override
	public Response handle(String request, Session session, IHTTPSession httpSession) {
		int number = -1;

		if(cloud == null) {
			return newFixedLengthResponse("DEBUG :D");
		}

		switch(request) {
			case "playeramount":
				number = cloud.getPlayers().size();
				break;
			case "serveramount":
				number = cloud.getServers().size();
				break;
			case "proxyamount":
				number = cloud.getBungeeCordProxies().size();
				break;
			case "wrapperamount":
				number = cloud.getWrappers().size();
				break;
		}

		return newFixedLengthResponse(number + "");
	}

	@Override
	public boolean needsAuthToken() {
		return false;
	}
}