package de.syscy.kagecloud.webserver.backendhandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.syscy.kagecloud.CloudServer;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.webserver.BackendRequestHandler;
import de.syscy.kagecloud.webserver.Session;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerDataTableHandler extends BackendRequestHandler {
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

		Stream<CloudServer> stream = cloud.getServers().values().parallelStream();
		//		Stream<CloudServer> stream = createRandomServers().parallelStream();

		long recordsTotal = cloud.getServers().size();

		if(!search.isEmpty()) {
			final String finalSearch = search.toLowerCase();

			stream = stream.filter(new Predicate<CloudServer>() {
				@Override
				public boolean test(CloudServer server) {
					return server.getName().toLowerCase().contains(finalSearch);
				}
			});
		}

		stream = column == 1 ? stream.sorted(new NameComparator(ordering)) : stream.sorted();

		Object[] tempArray = stream.toArray();

		long recordsFiltered = tempArray.length;

		Stream<Object> stream2 = Stream.of(tempArray);

		stream2 = stream2.skip(start);
		stream2 = stream2.limit(length);

		List<String[]> data = new ArrayList<>();
		stream2.spliterator().forEachRemaining(new Consumer<Object>() {
			@Override
			public void accept(Object serverObject) {
				CloudServer server = (CloudServer) serverObject;

				data.add(new String[] { server.getName(), server.getPlayers().size() + "" });
			}
		});

		DataTableResponse dataTableResponse = new DataTableResponse(draw, recordsTotal, recordsFiltered, data);

		return newFixedLengthResponse(Response.Status.OK, "application/json", gson.toJson(dataTableResponse));
	}

	//	private List<CloudServer> createRandomServers() {
	//		if(debugList == null) {
	//			debugList = new ArrayList<>();
	//
	//			debugList.add(new CloudServer(null, "lobby1", null, false, "", true));
	//			debugList.add(new CloudServer(null, "pyramid2", null, false, "", false));
	//			debugList.add(new CloudServer(null, "pyramid3", null, false, "", false));
	//			debugList.add(new CloudServer(null, "lobby1", null, false, "", true));
	//		}
	//		return debugList;
	//	}

	@RequiredArgsConstructor
	private class NameComparator implements Comparator<CloudServer> {
		private final boolean asc;

		@Override
		public int compare(CloudServer s1, CloudServer s2) {
			int i = s1.getName().compareToIgnoreCase(s2.getName());

			return asc ? i : -i;
		}
	}
}