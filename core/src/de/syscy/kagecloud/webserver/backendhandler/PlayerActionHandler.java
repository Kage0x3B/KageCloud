package de.syscy.kagecloud.webserver.backendhandler;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.chat.TextComponent;
import de.syscy.kagecloud.util.ChatColor;
import de.syscy.kagecloud.util.UUID;
import de.syscy.kagecloud.webserver.BackendRequestHandler;
import de.syscy.kagecloud.webserver.Session;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerActionHandler extends BackendRequestHandler {
	private final KageCloudCore cloud;

	@Override
	public Response handle(String request, Session session, IHTTPSession httpSession) {
		if(cloud == null) {
			return newFixedLengthResponse("DEBUG :D");
		}

		String[] args = getArgs(httpSession);

		if(args.length < 1) {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Wrong args");
		}

		UUID playerId = UUID.fromString(args[0]);

		if(playerId == null) {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Invalid player");
		}

		CloudPlayer player = cloud.getPlayers().get(playerId);

		if(player == null) {
			return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Invalid player");
		}

		switch(request) {
			case "kickplayer":
				player.kick("You were kicked from the network.");
				break;
			case "messageplayer":
				if(args.length < 2) {
					return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "Wrong args");
				}

				String message = ChatColor.translateAlternateColorCodes('&', args[1]);
				player.sendMessage(TextComponent.fromLegacyText(message));
				break;
		}

		return newFixedLengthResponse("");
	}
}