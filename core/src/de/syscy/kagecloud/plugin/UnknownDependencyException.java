package de.syscy.kagecloud.plugin;

public class UnknownDependencyException extends RuntimeException {
	private static final long serialVersionUID = 5721389371901775895L;

	public UnknownDependencyException(Throwable throwable) {
		super(throwable);
	}

	public UnknownDependencyException(String message) {
		super(message);
	}

	public UnknownDependencyException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public UnknownDependencyException() {
	}
}