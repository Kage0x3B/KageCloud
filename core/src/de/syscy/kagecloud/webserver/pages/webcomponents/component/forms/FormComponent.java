package de.syscy.kagecloud.webserver.pages.webcomponents.component.forms;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons.ButtonComponent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FormComponent extends BSComponent {
	private @Getter boolean inline = false;
	private @Getter String action = "";
	private @Getter String method = "";

	@Override
	public String toString() {
		StringBuilder formAttributes = new StringBuilder();

		formAttributes.append("class=\"form" + (inline ? "-inline" : "") + "\"");
		formAttributes.append("role=\"form\"");
		if(action != null && !action.isEmpty()) {
			formAttributes.append(" action=\"" + action + "\"");
		}
		if(method != null && !method.isEmpty()) {
			formAttributes.append(" method=\"" + method + "\"");
		}

		return "<form " + formAttributes + ">" + componentsToString() + "</form>";
	}

	public FormGroupComponent addFormGroup() {
		return addRC(new FormGroupComponent());
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

	public FormComponent setAction(String action) {
		this.action = action;

		return this;
	}

	public FormComponent setMethod(String method) {
		this.method = method;

		return this;
	}

	public FormComponent setInline(boolean inline) {
		this.inline = inline;

		return this;
	}
}