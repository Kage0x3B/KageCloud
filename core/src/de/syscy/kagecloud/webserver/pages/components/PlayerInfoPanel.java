package de.syscy.kagecloud.webserver.pages.components;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.util.ProtocolConstants;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.IconComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.PanelComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.htmltags.PComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.htmltags.SpanComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ColumnAttribute;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.PanelType;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.SCIcon;

public class PlayerInfoPanel extends PanelComponent {
	public PlayerInfoPanel(CloudPlayer player) {
		super(PanelType.PRIMARY, ColumnAttribute.LG_4, new IconComponent(SCIcon.Web.STICKY_NOTE).addRS(" Player Info"));

		add(new PComponent().addRS("UUID: ").addRS(new SpanComponent("id=\"playerId\"").addRS(player.getId().toString())));
		add(new PComponent().addRS("Current server: " + (player.getCurrentServer() == null ? "No server" : player.getCurrentServer().getName())));
		add(new PComponent().addRS("Current proxy: " + player.getBungeeCordProxy().getName()));
		add(new PComponent().addRS("Minecraft Version: " + ProtocolConstants.getVersionName(player.getVersion()) + " (" + player.getVersion() + ")"));
		add(new PComponent().addRS("Cloud Operator: " + player.isOp()));
	}
}