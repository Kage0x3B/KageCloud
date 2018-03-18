package de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ButtonSize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ButtonGroupComponent extends BSComponent {
	private @Getter ButtonSize size = null;
	private @Getter boolean justified = false;

	public ButtonGroupComponent(ButtonSize size) {
		this(size, false);
	}

	public ButtonGroupComponent(boolean justified) {
		this(null, justified);
	}

	@Override
	public String toString() {
		String classes = "btn-group " + (size == null ? "" : size.getCSSClass(true)) + (justified ? " btn-group-justified" : "");

		return "<div class=\"btn-group " + classes + "\" role=\"group\" aria-label=\"...\">" + componentsToString() + "</div>";
	}

	public ButtonComponent addButton(String text) {
		return addRC(new ButtonComponent(text));
	}

	public ButtonComponent addButton(BSComponent text) {
		return addRC(new ButtonComponent(text));
	}

	public LinkButtonComponent addButton(String text, String link) {
		return addRC(new LinkButtonComponent(text, link));
	}

	public LinkButtonComponent addButton(BSComponent text, String link) {
		return addRC(new LinkButtonComponent(text, link));
	}

	public ButtonGroupComponent setSize(ButtonSize size) {
		this.size = size;

		return this;
	}

	public ButtonGroupComponent setJustified(boolean justified) {
		this.justified = justified;

		return this;
	}
}