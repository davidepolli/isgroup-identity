package it.isgroup.identity.exception;

public class EmailAlreadyInUseException extends RuntimeException {

	public EmailAlreadyInUseException() {

	}

	public EmailAlreadyInUseException(String message) {
		super(message);
	}

	public EmailAlreadyInUseException(Throwable cause) {
		super(cause);
	}

	public EmailAlreadyInUseException(String message, Throwable cause) {
		super(message, cause);
	}
}
