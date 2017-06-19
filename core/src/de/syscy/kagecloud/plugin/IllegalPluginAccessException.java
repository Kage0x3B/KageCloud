package de.syscy.kagecloud.plugin;

public class IllegalPluginAccessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IllegalPluginAccessException() {
	}

	public IllegalPluginAccessException(String msg) {
		super(msg);
	}
}