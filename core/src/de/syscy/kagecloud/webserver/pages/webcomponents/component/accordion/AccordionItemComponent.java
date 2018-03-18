package de.syscy.kagecloud.webserver.pages.webcomponents.component.accordion;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.TextComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.PanelType;
import lombok.Getter;
import lombok.Setter;

public class AccordionItemComponent extends BSComponent {
	private @Getter @Setter PanelType type = PanelType.DEFAULT;

	private @Getter BSComponent title;

	public AccordionItemComponent() {

	}

	public AccordionItemComponent(String title) {
		this(new TextComponent(title));
	}

	public AccordionItemComponent(BSComponent title) {
		this(new TextComponent(title), null);
	}

	public AccordionItemComponent(String title, String content) {
		this(new TextComponent(title), new TextComponent(content));
	}

	public AccordionItemComponent(String title, BSComponent content) {
		this(new TextComponent(title), content);
	}

	public AccordionItemComponent(BSComponent title, BSComponent content) {
		this.title = title;

		if(content != null) {
			components.add(content);
		}
	}

	public String toString(String accordionId, int counter) {
		StringBuilder accordionItem = new StringBuilder();

		String itemId = accordionId + "I" + counter;

		accordionItem.append("<div class=\"panel " + type + "\">");

		accordionItem.append("<div class=\"panel-heading\">");
		accordionItem.append("<h4 class=\"panel-title\">");
		accordionItem.append("<a data-toggle=\"collapse\" data-parent=\"#" + accordionId + "\" href=\"#" + itemId + "\">" + title + "</a>");
		accordionItem.append("</h4>");
		accordionItem.append("</div>");

		accordionItem.append("<div id=\"" + itemId + "\" class=\"panel-collapse collapse\">");
		accordionItem.append("<div class=\"panel-body\">");
		accordionItem.append(componentsToString());
		accordionItem.append("</div>");
		accordionItem.append("</div>");

		accordionItem.append("</div>");

		return accordionItem.toString();
	}

	public void setTitle(BSComponent title) {
		this.title = title;
	}

	public void setTitle(String title) {
		this.title = new TextComponent(title);
	}
}