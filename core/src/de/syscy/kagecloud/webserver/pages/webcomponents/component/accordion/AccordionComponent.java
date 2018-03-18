package de.syscy.kagecloud.webserver.pages.webcomponents.component.accordion;

import com.google.common.base.Preconditions;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
public class AccordionComponent extends BSComponent {
	private @Getter String accordionId = "acc" + hashCode();

	@Override
	public String toString() {
		StringBuilder accordion = new StringBuilder();

		accordionId = "acc" + hashCode();

		accordion.append("<div class=\"panel-group\" id=\"" + accordionId + "\">");

		int counter = 0;

		for(BSComponent component : components) {
			accordion.append(((AccordionItemComponent) component).toString(accordionId, counter));
			counter++;
		}

		accordion.append("</div>");

		return accordion.toString();
	}

	@Override
	public void add(BSComponent component) {
		Preconditions.checkArgument(component instanceof AccordionItemComponent, "can't add other components than BSAccordionItems to a BSAccordion");

		super.add(component);
	}
}