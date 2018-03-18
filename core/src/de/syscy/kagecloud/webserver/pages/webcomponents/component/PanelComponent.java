package de.syscy.kagecloud.webserver.pages.webcomponents.component;

import de.syscy.kagecloud.webserver.pages.webcomponents.util.ColumnAttribute;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.PanelType;
import lombok.Getter;
import lombok.Setter;

public class PanelComponent extends BSComponent {
	private @Getter @Setter PanelType type = PanelType.DEFAULT;
	private @Getter @Setter ColumnAttribute columnAttribute = ColumnAttribute.MD_6;

	private @Getter BSComponent panelHeading;
	private @Getter BSComponent panelFooter;

	private @Getter boolean loginPanel;

	public PanelComponent(PanelType type, String panelHeading) {
		this(type, null, new TextComponent(panelHeading), null);
	}

	public PanelComponent(PanelType type, ColumnAttribute columnAttribute, String panelHeading) {
		this(type, columnAttribute, new TextComponent(panelHeading), null);
	}

	public PanelComponent(PanelType type, ColumnAttribute columnAttribute, BSComponent panelHeading) {
		this(type, columnAttribute, panelHeading, null);
	}

	public PanelComponent(PanelType type, ColumnAttribute columnAttribute, String panelHeading, String panelFooter) {
		this(type, columnAttribute, new TextComponent(panelHeading), new TextComponent(panelFooter));
	}

	public PanelComponent(PanelType type, ColumnAttribute columnAttribute, BSComponent panelHeading, BSComponent panelFooter) {
		this.type = type;
		this.columnAttribute = columnAttribute;
		this.panelHeading = panelHeading;
		this.panelFooter = panelFooter;
	}

	@Override
	public String toString() {
		StringBuilder panel = new StringBuilder();

		if(columnAttribute != null) {
			panel.append("<div class=\"" + columnAttribute + "\">");
		}

		panel.append("<div class=\"" + (loginPanel ? "login-panel " : "") + "panel " + type + "\">");

		if(panelHeading != null) {
			panel.append("<div class=\"panel-heading\">");
			panel.append(panelHeading);
			panel.append("</div>");
		}

		panel.append("<div class=\"panel-body\">");
		panel.append(componentsToString());
		panel.append("</div>");

		if(panelFooter != null) {
			panel.append("<div class=\"panel-footer\">");
			panel.append(panelFooter);
			panel.append("</div>");
		}

		panel.append("</div>");

		if(columnAttribute != null) {
			panel.append("</div>");
		}

		return panel.toString();
	}

	public PanelComponent setPanelHeading(BSComponent panelHeading) {
		this.panelHeading = panelHeading;

		return this;
	}

	public PanelComponent setPanelFooter(BSComponent panelFooter) {
		this.panelFooter = panelFooter;

		return this;
	}

	public PanelComponent setPanelHeading(String panelHeading) {
		this.panelHeading = new TextComponent(panelHeading);

		return this;
	}

	public PanelComponent setPanelFooter(String panelFooter) {
		this.panelFooter = new TextComponent(panelFooter);

		return this;
	}

	public PanelComponent setLoginPanel(boolean loginPanel) {
		this.loginPanel = loginPanel;

		return this;
	}
}