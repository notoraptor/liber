package liber.request.reception;

import liber.Libersaurus;
import liber.request.ReceivedRequest;

public class LinkCancelledReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() {
		Libersaurus.current.deleteInlink(sender(), secret());
	}
}
