package de.syscy.kagecloud.webserver.pages.webcomponents.component.htmltags;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class HComponent extends BSComponent {
	private @Getter int size = 1;
	private @Getter String attributes = "";

	public HComponent(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "<h" + size + " " + attributes + ">" + componentsToString() + "</h" + size + ">";
	}

	public HComponent setAttributes(String attributes) {
		this.attributes = attributes;

		return this;
	}
}