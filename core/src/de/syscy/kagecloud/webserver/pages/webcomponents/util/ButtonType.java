package de.syscy.kagecloud.webserver.pages.webcomponents.util;

public enum ButtonType {
	//@formatter:off
	DEFAULT, PRIMARY, SUCCESS, INFO, WARNING, DANGER;
	//@formatter:on

	public String getCSSClass() {
		return "btn-" + name().toLowerCase();
	}

	@Override
	public String toString() {
		return getCSSClass();
	}
}