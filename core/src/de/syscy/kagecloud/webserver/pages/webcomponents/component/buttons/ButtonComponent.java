package de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons;

import de.syscy.kagecloud.util.UUID;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.IconComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.RootPageContainer;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.TextComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ButtonLook;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ButtonSize;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ButtonType;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.SCIcon;

import lombok.Getter;

public class ButtonComponent extends BSComponent {
	private @Getter String buttonId = null;

	private @Getter ButtonType type = ButtonType.DEFAULT;
	private @Getter ButtonLook look = ButtonLook.DEFAULT;
	private @Getter ButtonSize size = ButtonSize.MD;
	private @Getter boolean fillRow = false;
	private @Getter String attributes = "";
	private @Getter String extraClasses = "";
	private @Getter boolean disabled = false;
	private @Getter String name;

	public ButtonComponent(String text) {
		this(new TextComponent(text));
	}

	public ButtonComponent(String text, SCIcon icon) {
		this(new IconComponent(icon).addRS(text.startsWith(" ") ? text : " " + text));
	}

	public ButtonComponent(BSComponent text) {
		if(text != null) {
			add(text);
		}
	}

	@Override
	public String toString() {
		String classes = "btn " + type + " " + look + " " + size + (fillRow ? " btn-block" : "") + (disabled ? " disabled " : " ") + extraClasses;
		String idString = buttonId != null ? "id=\"" + buttonId + "\" " : " ";

		return "<button " + idString + "class=\"" + classes + "\" " + attributes + ">" + componentsToString() + "</button>";
	}

	public ButtonComponent setClickListener(RootPageContainer rootPageContainer, ButtonClickListener clickListener) {
		if(buttonId != null) {
			throw new IllegalStateException("A button can't have more than one click listener");
		}

		buttonId = UUID.randomUUID().toString();

		rootPageContainer.getSession().getClickListeners().put(buttonId, clickListener);
		rootPageContainer.getJavaScript().add("ajaxButton(" + buttonId + ");");

		return this;
	}

	public ButtonComponent setType(ButtonType type) {
		this.type = type;

		return this;
	}

	public ButtonComponent setLook(ButtonLook look) {
		this.look = look;

		return this;
	}

	public ButtonComponent setSize(ButtonSize size) {
		this.size = size;

		return this;
	}

	public ButtonComponent setFillRow(boolean fillRow) {
		this.fillRow = fillRow;

		return this;
	}

	public ButtonComponent setAttributes(String attributes) {
		this.attributes = attributes;

		return this;
	}

	public ButtonComponent setExtraClasses(String extraClasses) {
		this.extraClasses = extraClasses;

		return this;
	}

	public ButtonComponent setDisabled(boolean disabled) {
		this.disabled = disabled;

		return this;
	}

	public ButtonComponent setName(String name) {
		this.name = name;

		return this;
	}
}