package de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons;

import de.syscy.kagecloud.util.UUID;
import de.syscy.kagecloud.webserver.Session;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.IconComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.RootPageContainer;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.TextComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ButtonLook;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ButtonSize;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ButtonType;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.SCIcon;

import lombok.Getter;

public class JSButtonComponent extends BSComponent {
	private @Getter String buttonId;
	private RootPageContainer rootPageContainer;

	private @Getter ButtonType type = ButtonType.DEFAULT;
	private @Getter ButtonLook look = ButtonLook.DEFAULT;
	private @Getter ButtonSize size = ButtonSize.MD;
	private @Getter boolean fillRow = false;
	private @Getter String attributes = "";
	private @Getter String extraClasses = "";
	private @Getter boolean disabled = false;
	private @Getter String name;

	public JSButtonComponent(String text, String js, RootPageContainer rootPageContainer) {
		this(new TextComponent(text), js, rootPageContainer);
	}

	public JSButtonComponent(String text, SCIcon icon, String js, RootPageContainer rootPageContainer) {
		this(new IconComponent(icon).addRS(text.startsWith(" ") ? text : " " + text), js, rootPageContainer);
	}

	public JSButtonComponent(BSComponent text, String js, RootPageContainer rootPageContainer) {
		if(text != null) {
			add(text);
		}

		buttonId = UUID.randomUUID().toString();

		rootPageContainer.getJavaScript().add("$('#" + buttonId + "').click(function() {" + js + "});");
	}

	@Override
	public String toString() {
		String classes = "btn " + type + " " + look + " " + size + (fillRow ? " btn-block" : "") + (disabled ? " disabled " : " ") + extraClasses;

		return "<button id=\"" + buttonId + "\" class=\"" + classes + "\" " + attributes + ">" + componentsToString() + "</button>";
	}

	public JSButtonComponent addClickListener(ButtonClickListener clickListener, Session session) {
		session.getClickListeners().put(buttonId, clickListener);
		rootPageContainer.getJavaScript().add("ajaxButton(" + buttonId + ");");

		return this;
	}

	public JSButtonComponent setType(ButtonType type) {
		this.type = type;

		return this;
	}

	public JSButtonComponent setLook(ButtonLook look) {
		this.look = look;

		return this;
	}

	public JSButtonComponent setSize(ButtonSize size) {
		this.size = size;

		return this;
	}

	public JSButtonComponent setFillRow(boolean fillRow) {
		this.fillRow = fillRow;

		return this;
	}

	public JSButtonComponent setAttributes(String attributes) {
		this.attributes = attributes;

		return this;
	}

	public JSButtonComponent setExtraClasses(String extraClasses) {
		this.extraClasses = extraClasses;

		return this;
	}

	public JSButtonComponent setDisabled(boolean disabled) {
		this.disabled = disabled;

		return this;
	}

	public JSButtonComponent setName(String name) {
		this.name = name;

		return this;
	}
}