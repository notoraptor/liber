package liber.request.reception;

import liber.Libersaurus;
import liber.request.ReceivedRequest;

public class LinkRefusedReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() {
		Libersaurus.current.deleteOutlink(sender(), secret());
	}
}
