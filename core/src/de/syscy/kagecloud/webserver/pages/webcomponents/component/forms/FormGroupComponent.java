package de.syscy.kagecloud.webserver.pages.webcomponents.component.forms;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons.ButtonComponent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FormGroupComponent extends BSComponent {
	@Override
	public String toString() {
		return "<div class=\"form-group\">" + componentsToString() + "</div>";
	}

	public TextBoxComponent addTextBox(String name) {
		return addRC(new TextBoxComponent(name));
	}

	public ButtonComponent addButton(String text) {
		return addRC(new ButtonComponent(text));
	}

	public ButtonComponent addButton(BSComponent text) {
		return addRC(new ButtonComponent(text));
	}

	public HiddenFormComponent addHidden(String name, String value) {
		return addRC(new HiddenFormComponent(name, value));
	}
}