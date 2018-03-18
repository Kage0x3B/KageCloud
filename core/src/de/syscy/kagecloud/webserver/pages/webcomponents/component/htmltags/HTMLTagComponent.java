package de.syscy.kagecloud.webserver.pages.webcomponents.component.htmltags;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class HTMLTagComponent extends BSComponent {
	private final @Getter String tag;
	private @Getter String attributes = "";

	@Override
	public String toString() {
		return "<" + tag + " " + attributes + ">" + componentsToString() + "</" + tag + ">";
	}

	public HTMLTagComponent setAttributes(String attributes) {
		this.attributes = attributes;

		return this;
	}
}