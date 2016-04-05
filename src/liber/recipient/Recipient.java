package liber.recipient;

import liber.request.requestSent.Request;
import liber.request.Response;

public interface Recipient {
	String address();
	String serverAddress();
	Response receive(Request request) throws Exception;
	default boolean updatable() {
		return false;
	}
	default void update() throws Exception {}
}
