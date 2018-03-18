package de.syscy.kagecloud.webserver.pages.components;

import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.webserver.Session;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.PanelComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.RootPageContainer;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons.ButtonClickListener;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons.ButtonGroupComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons.LinkButtonComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ButtonType;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ColumnAttribute;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.PanelType;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class ControlPanel extends PanelComponent {
	public ControlPanel(KageCloudCore cloud, RootPageContainer rootPageContainer) {
		super(PanelType.PRIMARY, ColumnAttribute.LG_4, "Control Panel");

		ButtonGroupComponent buttonGroupComponent = addButtonGroup().setJustified(true);
		buttonGroupComponent.addButton("Restart").setType(ButtonType.WARNING).setClickListener(rootPageContainer, new ButtonClickListener() {
			@Override
			public void onButtonClick(String buttonId, Session session, IHTTPSession httpSession) {
				//				cloud.
			}
		});
		addRS(new LinkButtonComponent("Stop", "?a=s").setType(ButtonType.DANGER));
	}
}