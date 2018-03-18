package de.syscy.kagecloud.webserver.pages.webcomponents.component;

import lombok.Getter;
import lombok.Setter;

public class TextComponent extends BSComponent {
	private @Getter @Setter String text;

	public TextComponent(Object text) {
		this.text = text.toString();
	}

	@Override
	public String toString() {
		return text + super.toString();
	}
}