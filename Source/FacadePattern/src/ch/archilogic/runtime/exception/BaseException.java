package ch.archilogic.runtime.exception;

public abstract class BaseException extends Exception {
	private static final long serialVersionUID = 4392112812109614397L;

	public BaseException() {
		super();
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BaseException(Throwable cause) {
		super(cause);
	}	
}
