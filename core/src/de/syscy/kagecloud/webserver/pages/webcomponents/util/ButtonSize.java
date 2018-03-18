package de.syscy.kagecloud.webserver.pages.webcomponents.util;

public enum ButtonSize {
	//@formatter:off
	XS, SM, MD, LG;
	//@formatter:on

	public String getCSSClass(boolean group) {
		return this == MD ? "" : "btn-" + (group ? "group-" : "") + name().toLowerCase();
	}

	@Override
	public String toString() {
		return getCSSClass(false);
	}
}