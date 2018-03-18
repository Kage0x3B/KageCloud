package de.syscy.kagecloud.webserver.pages.webcomponents.component.htmltags;

public class DivComponent extends HTMLTagComponent {
	public DivComponent() {
		super("div");
	}

	public DivComponent(String attributes) {
		super("div");

		setAttributes(attributes);
	}
}