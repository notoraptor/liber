package liber.exception;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class AddressException extends Exception {
	public AddressException(String message) {
		super(message);
	}
	public AddressException(Throwable cause) {
		super(cause);
	}
}
