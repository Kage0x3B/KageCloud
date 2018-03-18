package de.syscy.kagecloud.webserver.pages;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.util.UUID;
import de.syscy.kagecloud.webserver.Session;
import de.syscy.kagecloud.webserver.pages.components.PlayerInfoPanel;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.IconComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.PanelComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.RootPageContainer;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons.JSButtonComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.forms.TextBoxComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ColumnAttribute;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.PanelType;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ResourceUtil;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.SCIcon;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerPage extends BasePage {
	private final KageCloudCore cloud;

	@Override
	public RootPageContainer constructPage(RootPageContainer page, String request, Session session, IHTTPSession httpSession) {
		String playerIdParameter = getParameter(httpSession, "id");

		if(playerIdParameter == null || playerIdParameter.isEmpty()) {
			return null;
		}

		UUID playerId = UUID.fromString(playerIdParameter);

		if(playerId == null) {
			return null;
		}

		CloudPlayer player = cloud.getPlayers().get(playerId);

		if(player == null) {
			return null;
		}

		page.setTitle(player.getName());
		page.addTitle();

		page.add(new PlayerInfoPanel(player));

		PanelComponent actionPanel = new PanelComponent(PanelType.WARNING, ColumnAttribute.LG_4, new IconComponent(SCIcon.Web.BOLT).addRS(" Actions"));

		JSButtonComponent kickButton = new JSButtonComponent("Kick", SCIcon.Web.BAN, ResourceUtil.readWebServerResource("js/player/kickButton.js"), page);
		actionPanel.add(kickButton);

		cloud.getPluginManager().callEvent(new ConstructPlayerPageEvent(page, request, session, httpSession, player, actionPanel));

		if(!actionPanel.isEmpty()) {
			page.add(actionPanel);
		}

		PanelComponent messagePanel = new PanelComponent(PanelType.INFO, ColumnAttribute.LG_4, new IconComponent(SCIcon.Web.MAIL_FORWARD).addRS(" Message Player"));

		messagePanel.add(new TextBoxComponent().setId("messageText").setPlaceholder("Message"));
		JSButtonComponent sendButton = new JSButtonComponent("Send", SCIcon.Web.SEND, ResourceUtil.readWebServerResource("js/player/sendMessageButton.js"), page);
		messagePanel.add(sendButton);

		page.add(messagePanel);

		return page;
	}

	public static class ConstructPlayerPageEvent extends ConstructPageEvent {
		private @Getter CloudPlayer player;
		private @Getter PanelComponent actionPanel;

		public ConstructPlayerPageEvent(RootPageContainer page, String request, Session session, IHTTPSession httpSession, CloudPlayer player, PanelComponent actionPanel) {
			super(page, request, session, httpSession);

			this.player = player;
			this.actionPanel = actionPanel;
		}
	}
}