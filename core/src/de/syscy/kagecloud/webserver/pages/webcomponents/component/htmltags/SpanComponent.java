package de.syscy.kagecloud.webserver.pages.webcomponents.component.htmltags;

public class SpanComponent extends HTMLTagComponent {
	public SpanComponent() {
		super("span");
	}

	public SpanComponent(String attributes) {
		super("span");

		setAttributes(attributes);
	}
}