package de.syscy.kagecloud.plugin;

public class AuthorNagException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String message;

	public AuthorNagException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}