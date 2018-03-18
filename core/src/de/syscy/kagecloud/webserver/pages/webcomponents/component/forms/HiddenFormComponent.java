package de.syscy.kagecloud.webserver.pages.webcomponents.component.forms;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class HiddenFormComponent extends BSComponent {
	private @Getter @Setter String name;
	private @Getter @Setter String value;

	public HiddenFormComponent(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString() {
		return "<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\">";
	}
}