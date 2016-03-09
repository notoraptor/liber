package liber.exception;

public class LibercardException extends Exception {
	public LibercardException(Exception cause) {
		super(cause);
	}
	public LibercardException(String message) {
		super(message);
	}
}
