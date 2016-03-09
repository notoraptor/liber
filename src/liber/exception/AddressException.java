package liber.exception;

public class AddressException extends Exception {
	public AddressException(String message) {
		super(message);
	}
	public AddressException(Throwable cause) {
		super(cause);
	}
}
