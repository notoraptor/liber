package liber.request.reception;

import liber.Libersaurus;
import liber.request.ReceivedRequest;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class LinkCancelledReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() {
		Libersaurus.current.deleteInlink(sender(), secret());
	}
}
