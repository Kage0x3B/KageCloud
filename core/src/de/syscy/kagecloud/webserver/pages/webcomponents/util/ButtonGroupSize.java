package de.syscy.kagecloud.webserver.pages.webcomponents.util;

public enum ButtonGroupSize {
	//@formatter:off
	XS, SM, MD, LG;
	//@formatter:on

	public String getCSSClass() {
		return this == MD ? "" : "btn-" + name().toLowerCase();
	}

	@Override
	public String toString() {
		return getCSSClass();
	}
}