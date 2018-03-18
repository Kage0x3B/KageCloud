package de.syscy.kagecloud.webserver.pages.webcomponents.component.forms;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TextBoxComponent extends BSComponent {
	private String id;
	private String name;
	private String type = "text";
	private String placeholder;
	private String value;
	private boolean autofocus;
	private boolean readonly;

	public TextBoxComponent(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder attributes = new StringBuilder();

		if(id != null && !id.isEmpty()) {
			attributes.append(" id=\"" + id + "\"");
		}

		attributes.append(" class=\"form-control\"");
		attributes.append(" name=\"" + name + "\"");
		attributes.append(" type=\"" + type + "\"");
		attributes.append(placeholder != null ? " placeholder=\"" + placeholder + "\"" : "");
		attributes.append(value != null ? " value=\"" + value + "\"" : "");
		attributes.append(autofocus ? " autofocus" : "");
		attributes.append(readonly ? " readonly" : "");

		return "<input " + attributes.toString() + ">";
	}
}