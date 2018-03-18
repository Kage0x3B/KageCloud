package de.syscy.kagecloud.webserver.pages.webcomponents.navigation;

import java.util.LinkedList;
import java.util.List;

import de.syscy.kagecloud.webserver.pages.webcomponents.util.SCIcon;

public class BSNavigation {
	private List<BSNavigationComponent> components = new LinkedList<>();

	public static BSNavigation getDefaultNavigation() {
		BSNavigation navigation = new BSNavigation();
		navigation.add(new BSNavigationButton("Dashboard", "/", SCIcon.Web.DASHBOARD));
		navigation.add(new BSNavigationButton("Players", "/players", SCIcon.Web.USERS));
		//		navigation.add(new BSNavigationButton("Main Settings", "/settings", SCIcon.Web.GEARS));

		//		if(!SyscyConnect.nodeNetwork.getNodes().isEmpty()) {
		//			BSNavigationDropdown nodesDropdown = new BSNavigationDropdown("Nodes", SCIcon.Web.SITEMAP);
		//
		//			for(Node node : SyscyConnect.nodeNetwork.getNodes()) {
		//				nodesDropdown.add(new BSNavigationButton(node.getName(), "/node/" + Util.getServerNameFromUUID(node.getNodeID().toString()), node.getIcon()));
		//			}
		//
		//			navigation.add(nodesDropdown);
		//		}

		//		if(!SyscyConnect.pluginManager.getPlugins().isEmpty()) {
		//			BSNavigationDropdown pluginDropdown = new BSNavigationDropdown("Plugins", SCIcon.Web.CUBES);
		//
		//			int otherPlugins = 0;
		//
		//			for(ISCPlugin plugin : SyscyConnect.pluginManager.getPlugins()) {
		//				if(plugin.constructPluginPage(new RootPageContainer("", true)) == null || plugin.getPluginSettings() == null) {
		//					otherPlugins++;
		//
		//					continue;
		//				}
		//
		//				SCPluginInfo pluginInfo = plugin.getPluginInfo();
		//
		//				pluginDropdown.add(new BSNavigationButton(pluginInfo.name, "/plugin/" + pluginInfo.id, pluginInfo.icon));
		//			}
		//
		//			if(otherPlugins > 0) {
		//				pluginDropdown.add(new BSNavigationButton("Other Plugins", "/plugin/others", otherPlugins > 1 ? SCIcon.Web.CUBES : SCIcon.Web.CUBE));
		//			}
		//
		//			navigation.add(pluginDropdown);
		//		}

		return navigation;
	}

	public void add(BSNavigationComponent component) {
		components.add(component);
	}

	@Override
	public String toString() {
		StringBuilder navigation = new StringBuilder();

		navigation.append("<nav class=\"navbar navbar-default navbar-static-top\" role=\"navigation\" style=\"margin-bottom: 0\">");

		navigation.append("<div class=\"navbar-header\">");
		navigation.append("<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-collapse\">");
		navigation.append("<span class=\"sr-only\">Toggle navigation</span>");
		navigation.append("<span class=\"icon-bar\"></span>");
		navigation.append("<span class=\"icon-bar\"></span>");
		navigation.append("<span class=\"icon-bar\"></span>");
		navigation.append("</button>");
		navigation.append("<a class=\"navbar-brand\" href=\"/\">KageCloud</a>");
		navigation.append("</div>");

		navigation.append("<div class=\"navbar-default sidebar\" role=\"navigation\">");
		navigation.append("<div class=\"sidebar-nav navbar-collapse\">");
		navigation.append("<ul class=\"nav\" id=\"side-menu\">");

		for(BSNavigationComponent component : components) {
			navigation.append("<li>");
			navigation.append(component);
			navigation.append("</li>");
		}

		navigation.append("</ul>");
		navigation.append("</div>");
		navigation.append("</div>");

		navigation.append("</nav>");

		return navigation.toString();
	}
}