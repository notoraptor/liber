package liber.request.requestReceived.reception;

import liber.Libersaurus;
import liber.request.requestReceived.ReceivedRequest;

public class LinkRefusedReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() {
		Libersaurus.current.deleteOutlink(sender(), secret());
	}
}
