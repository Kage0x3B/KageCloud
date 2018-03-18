package de.syscy.kagecloud.webserver.pages.webcomponents.component.buttons;

import de.syscy.kagecloud.webserver.pages.webcomponents.component.BSComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.IconComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.component.TextComponent;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ButtonLook;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ButtonSize;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.ButtonType;
import de.syscy.kagecloud.webserver.pages.webcomponents.util.SCIcon;
import lombok.Getter;

public class LinkButtonComponent extends BSComponent {
	private @Getter ButtonType type = ButtonType.DEFAULT;
	private @Getter ButtonLook look = ButtonLook.DEFAULT;
	private @Getter ButtonSize size = ButtonSize.MD;
	private @Getter String link = "#";
	private @Getter boolean fillRow = false;
	private @Getter String attributes = "";
	private @Getter boolean disabled = false;
	private @Getter String name;

	public LinkButtonComponent(String text, String link) {
		this(new TextComponent(text), link);
	}

	public LinkButtonComponent(SCIcon icon, String text, String link) {
		this(new IconComponent(icon).addRS(text.startsWith(" ") ? text : " " + text), link);
	}

	public LinkButtonComponent(BSComponent text, String link) {
		if(text != null) {
			add(text);
		}

		this.link = link;
	}

	@Override
	public String toString() {
		String classes = "btn " + type + " " + look + " " + size + (fillRow ? " btn-block" : "") + (disabled ? " disabled" : "");

		return "<a href=\"" + link + "\" class=\"" + classes + "\" " + attributes + ">" + componentsToString() + "</a>";
	}

	public LinkButtonComponent setType(ButtonType type) {
		this.type = type;

		return this;
	}

	public LinkButtonComponent setLook(ButtonLook look) {
		this.look = look;

		return this;
	}

	public LinkButtonComponent setSize(ButtonSize size) {
		this.size = size;

		return this;
	}

	public LinkButtonComponent setLink(String link) {
		this.link = link;

		return this;
	}

	public LinkButtonComponent setFillRow(boolean fillRow) {
		this.fillRow = fillRow;

		return this;
	}

	public LinkButtonComponent setAttributes(String attributes) {
		this.attributes = attributes;

		return this;
	}

	public LinkButtonComponent setDisabled(boolean disabled) {
		this.disabled = disabled;

		return this;
	}

	public LinkButtonComponent setName(String name) {
		this.name = name;

		return this;
	}
}