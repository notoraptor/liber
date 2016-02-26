package liber.exception;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class LibercardException extends Exception {
	public LibercardException(Exception cause) {
		super(cause);
	}
	public LibercardException(String message) {
		super(message);
	}
}
