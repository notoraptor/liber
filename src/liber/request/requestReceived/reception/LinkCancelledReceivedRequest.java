package liber.request.requestReceived.reception;

import liber.Libersaurus;
import liber.request.requestReceived.ReceivedRequest;

public class LinkCancelledReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() {
		Libersaurus.current.deleteInlink(sender(), secret());
	}
}
