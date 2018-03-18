package de.syscy.kagecloud.webserver.pages;

import de.syscy.kagecloud.webserver.Session;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.DataTableComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.RootPageContainer;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ResourceUtil;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class PlayersPage extends BasePage {
	@Override
	public RootPageContainer constructPage(RootPageContainer page, String request, Session session, IHTTPSession httpSession) {
		page.setTitle("Players");
		page.addTitle();

		String tableJs = ResourceUtil.readWebServerResource("js/player/playerTable.js");
		page.add(new DataTableComponent("playerTable", new String[] { "Name", "Server", "UUID" }, page, tableJs));

		return page;
	}
}