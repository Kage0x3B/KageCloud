package de.syscy.kagecloud.webserver.pages;

import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.webserver.Session;
import de.syscy.kagecloud.webserver.pages.components.ControlPanel;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.DashboardPanelComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.RootPageContainer;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.htmltags.DivComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.htmltags.DividerComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.PanelType;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.SCIcon;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DashboardPage extends BasePage {
	private final KageCloudCore cloud;

	@Override
	public RootPageContainer constructPage(RootPageContainer page, String request, Session session, IHTTPSession httpSession) {
		page.setTitle("Dashboard");
		page.addTitle();

		DivComponent dashboardPanels = page.addRow();
		dashboardPanels.add(new DashboardPanelComponent(PanelType.PRIMARY, SCIcon.Web.USERS, "Player", String.valueOf(cloud.getPlayers().size()), "/players"));
		dashboardPanels.add(new DashboardPanelComponent(PanelType.GREEN, SCIcon.Web.SERVER, "Server", String.valueOf(cloud.getServers().size()), "/servers"));
		dashboardPanels.add(new DashboardPanelComponent(PanelType.YELLOW, SCIcon.Web.SITEMAP, "BungeeCord Proxy", String.valueOf(cloud.getBungeeCordProxies().size()), "/proxies"));
		dashboardPanels.add(new DashboardPanelComponent(PanelType.RED, SCIcon.Web.CUBES, "Wrapper", String.valueOf(cloud.getWrappers().size()), "/wrappers"));

		page.add(new DividerComponent());
		page.add(new ControlPanel(cloud, page));

		cloud.getPluginManager().callEvent(new ConstructMainPageEvent(page, request, session, httpSession));

		return page;
	}

	public static class ConstructMainPageEvent extends ConstructPageEvent {
		public ConstructMainPageEvent(RootPageContainer page, String request, Session session, IHTTPSession httpSession) {
			super(page, request, session, httpSession);
		}
	}
}