package de.syscy.kagecloud.webserver.pages.webcomponents.util;

public enum PanelType {
	//@formatter:off
	DEFAULT, PRIMARY, SUCCESS, INFO, WARNING, DANGER, GREEN, YELLOW, RED, LOGIN;
	//@formatter:on

	public String getCSSClass() {
		return "panel-" + name().toLowerCase();
	}

	@Override
	public String toString() {
		return getCSSClass();
	}
}