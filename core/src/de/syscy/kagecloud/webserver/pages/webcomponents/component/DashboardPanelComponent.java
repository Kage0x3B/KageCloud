package de.syscy.kagecloud.webserver.pages.webcomponents.component;

import de.syscy.kagecloud.webserver.pages.webcomponents.util.PanelType;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.SCIcon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DashboardPanelComponent extends BSComponent {
	private PanelType type = PanelType.DEFAULT;

	private SCIcon panelIcon;
	private BSComponent panelTitle;
	private BSComponent panelValue;
	private BSComponent panelFooter;
	private String footerUrl;

	public DashboardPanelComponent(PanelType type, SCIcon panelIcon, String panelTitle, String panelValue, String footerUrl) {
		this(type, panelIcon, panelTitle, panelValue, "View Details", footerUrl);
	}

	public DashboardPanelComponent(PanelType type, SCIcon panelIcon, String panelTitle, String panelValue, String panelFooter, String footerUrl) {
		this(type, panelIcon, new TextComponent(panelTitle), new TextComponent(panelValue), new TextComponent(panelFooter), footerUrl);
	}

	public DashboardPanelComponent(PanelType type, SCIcon panelIcon, BSComponent panelTitle, BSComponent panelValue, BSComponent panelFooter, String footerUrl) {
		this.type = type;
		this.panelIcon = panelIcon;
		this.panelTitle = panelTitle;
		this.panelValue = panelValue;
		this.panelFooter = panelFooter;
		this.footerUrl = footerUrl;
	}

	@Override
	public String toString() {
		StringBuilder panel = new StringBuilder();

		panel.append("<div class=\"col-lg-3 col-md-6\">");
		panel.append("<div class=\"panel " + type + "\">");
		panel.append("<div class=\"panel-heading\">");
		panel.append("<div class=\"row\">");
		panel.append("<div class=\"col-xs-3\">");
		panel.append("<i class=\"fa " + panelIcon + " fa-5x\"></i>");
		panel.append("</div>");
		panel.append("<div class=\"col-xs-9 text-right\">");
		panel.append("<div class=\"huge\">");
		panel.append(panelValue);
		panel.append("</div>");
		panel.append("<div>");
		panel.append(panelTitle);
		panel.append("</div>");
		panel.append("</div>");
		panel.append("</div>");
		panel.append("</div>");

		panel.append("<a href=\"" + footerUrl + "\">");
		panel.append("<div class=\"panel-footer\">");
		panel.append("<span class=\"pull-left\">" + panelFooter + "</span>");
		panel.append("<span class=\"pull-right\"><i class=\"fa fa-arrow-circle-right\"></i></span>");
		panel.append("<div class=\"clearfix\"></div>");
		panel.append("</div>");
		panel.append("</a>");
		panel.append("</div>");
		panel.append("</div>");

		return panel.toString();
	}
}