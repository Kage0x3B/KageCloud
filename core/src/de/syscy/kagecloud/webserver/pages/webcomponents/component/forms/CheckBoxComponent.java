package de.syscy.kagecloud.webserver.pages.webcomponents.component.forms;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CheckBoxComponent extends BSComponent {
	private @Getter String name;
	private @Getter boolean checked;

	public CheckBoxComponent(String name) {
		this(name, false);
	}

	@Override
	public String toString() {
		StringBuilder attributes = new StringBuilder();
		attributes.append(" class=\"form-control\"");
		attributes.append(" type=\"checkbox\"");
		attributes.append(" name=\"" + name + "\"");
		attributes.append(" value=\"1\"");
		attributes.append(checked ? " checked" : "");

		return "<input " + attributes.toString() + ">";
	}

	public CheckBoxComponent setName(String name) {
		this.name = name;

		return this;
	}

	public CheckBoxComponent setChecked(boolean checked) {
		this.checked = checked;

		return this;
	}
}