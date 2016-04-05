package liber.request.requestReceived.reception;

import liber.Libersaurus;
import liber.exception.RequestException;
import liber.request.requestReceived.ReceivedRequest;

public class LinkDeletedReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() throws RequestException {
		Libersaurus.current.checkContactDeletion(sender(), secret());
	}
}
