package de.syscy.kagecloud.webserver.pages.webcomponents.component.htmltags;

public class DividerComponent extends HTMLTagComponent {
	public DividerComponent() {
		super("hr");
	}

	@Override
	public String toString() {
		return "<hr />";
	}
}