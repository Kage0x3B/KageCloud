package de.syscy.kagecloud.webserver.pages.webcomponents.util;

public enum NavigationDropdownLevel {
	//@formatter:off
	SECOND, THIRD;
	//@formatter:on

	public String getCSSClass() {
		return "nav-" + name().toLowerCase() + "-level";
	}

	@Override
	public String toString() {
		return getCSSClass();
	}
}