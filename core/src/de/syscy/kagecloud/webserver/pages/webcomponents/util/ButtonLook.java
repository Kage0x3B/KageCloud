package de.syscy.kagecloud.webserver.pages.webcomponents.util;

public enum ButtonLook {
	//@formatter:off
	DEFAULT, OUTLINE, CIRCLE;
	//@formatter:on

	public String getCSSClass() {
		return this == DEFAULT ? "" : "btn-" + name().toLowerCase();
	}

	@Override
	public String toString() {
		return getCSSClass();
	}
}