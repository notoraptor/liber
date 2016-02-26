package liber.exception;

/**
 * Created by HPPC on 21/02/2016.
 */
public class LibercardException extends Exception {
	public LibercardException(Exception cause) {
		super(cause);
	}
	public LibercardException(String message) {
		super(message);
	}
}
