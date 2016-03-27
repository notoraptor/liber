package liber.exception;

import liber.request.Response;

public class AddressException extends Exception {
	static private final int FROM_CAUSE = 0;
	static private final int FROM_RESPONSE = 1;
	static private final int FROM_PORT = 2;
	private int origin;
	public AddressException(Throwable cause) {
		super(cause);
		origin = FROM_CAUSE;
	}
	public AddressException(Response response) {
		super(response.status());
		origin = FROM_RESPONSE;
	}
	public AddressException(int port) {
		super(String.valueOf(port));
		origin = FROM_PORT;
	}
	public boolean isFromResponse() {
		return origin == FROM_RESPONSE;
	}
	public boolean responseIsErrorUsername() {
		return getMessage().equals("ERROR_USERNAME");
	}
}
