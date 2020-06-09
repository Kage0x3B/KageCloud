package de.syscy.kagecloud.webserver.backendhandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.webserver.BackendRequestHandler;
import de.syscy.kagecloud.webserver.Session;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerDataTableHandler extends BackendRequestHandler {
	private final KageCloudCore cloud;

	private final Gson gson = new GsonBuilder().create();

	@Override
	public Response handle(String request, Session session, IHTTPSession httpSession) {
		int draw = Integer.parseInt(getParameter(httpSession, "draw"));
		int start = Integer.parseInt(getParameter(httpSession, "start"));
		int length = Integer.parseInt(getParameter(httpSession, "length"));
		String search = getParameter(httpSession, "search[value]");

		String orderingString = getParameter(httpSession, "order[0][dir]");
		int column = Integer.parseInt(getParameter(httpSession, "order[0][column]"));

		boolean ordering = orderingString.toLowerCase().equalsIgnoreCase("asc");

		//		List<CloudPlayer> debugList = createRandomPlayers();

		Stream<CloudPlayer> stream = cloud.getPlayers().values().parallelStream();
		//		Stream<CloudPlayer> stream = debugList.parallelStream();

		long recordsTotal = cloud.getPlayers().size();

		if(!search.isEmpty()) {
			final String finalSearch = search.toLowerCase();

			stream = stream.filter(new Predicate<CloudPlayer>() {
				@Override
				public boolean test(CloudPlayer player) {
					return player.getName().toLowerCase().contains(finalSearch) || (player.getCurrentServer() != null && player.getCurrentServer().getName().toLowerCase().contains(finalSearch));
				}
			});
		}

		stream = stream.sorted(column == 0 ? new NameComparator(ordering) : new ServerComparator(ordering));

		Object[] tempArray = stream.toArray();

		long recordsFiltered = tempArray.length;

		Stream<Object> stream2 = Stream.of(tempArray);

		stream2 = stream2.skip(start);
		stream2 = stream2.limit(length);

		List<String[]> data = new ArrayList<>();
		stream2.spliterator().forEachRemaining(new Consumer<Object>() {
			@Override
			public void accept(Object playerObject) {
				CloudPlayer player = (CloudPlayer) playerObject;

				String serverName = player.getCurrentServer() == null ? "" : player.getCurrentServer().getName();
				data.add(new String[] { player.getName(), serverName, player.getId().toString() });
			}
		});

		DataTableResponse dataTableResponse = new DataTableResponse(draw, recordsTotal, recordsFiltered, data);

		return newFixedLengthResponse(Response.Status.OK, "application/json", gson.toJson(dataTableResponse));
	}

	//	private List<CloudPlayer> createRandomPlayers() {
	//		if(debugList == null) {
	//			debugList = new ArrayList<>();
	//
	//			ThreadLocalRandom r = ThreadLocalRandom.current();
	//			CloudServer s1 = new CloudServer(null, "lobby1", null, false, "", true);
	//			CloudServer s2 = new CloudServer(null, "pyramid2", null, false, "", false);
	//
	//			for(int i = 0; i < 100; i++) {
	//				String name = "";
	//				int letters = r.nextInt(5, 15);
	//				for(int j = 0; j < letters; j++) {
	//					name += (char) ('a' + r.nextInt(25));
	//				}
	//				CloudPlayer player = new CloudPlayer(UUID.randomUUID(), name, 1);
	//				player.setCurrentServer(r.nextBoolean() ? s1 : s2);
	//				debugList.add(player);
	//			}
	//		}
	//		return debugList;
	//	}

	@RequiredArgsConstructor
	private class NameComparator implements Comparator<CloudPlayer> {
		private final boolean asc;

		@Override
		public int compare(CloudPlayer p1, CloudPlayer p2) {
			int i = p1.getName().compareToIgnoreCase(p2.getName());

			return asc ? i : -i;
		}
	}

	@RequiredArgsConstructor
	private class ServerComparator implements Comparator<CloudPlayer> {
		private final boolean asc;

		@Override
		public int compare(CloudPlayer p1, CloudPlayer p2) {
			String s1 = p1.getCurrentServer() == null ? "" : p1.getCurrentServer().getName();
			String s2 = p2.getCurrentServer() == null ? "" : p2.getCurrentServer().getName();

			int i = s1.compareToIgnoreCase(s2);

			return asc ? i : -i;
		}
	}
}