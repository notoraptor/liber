package liber.recipient;

import liber.request.Request;
import liber.request.Response;

public interface Recipient {
	String address();
	String serverAddress();
	Response receive(Request request) throws Exception;
	default boolean updatable() {
		return false;
	}
	default void update() {}
}
