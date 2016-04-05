package liber.request.requestReceived.reception;

import liber.Libersaurus;
import liber.exception.RequestException;
import liber.request.requestReceived.ReceivedRequest;

public class LinkAcceptedReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() throws RequestException {
		Libersaurus.current.validateContact(sender(), secret());
	}
}
