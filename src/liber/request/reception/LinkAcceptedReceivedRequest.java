package liber.request.reception;

import liber.Libersaurus;
import liber.exception.RequestException;
import liber.request.ReceivedRequest;

/**
 * Created by HPPC on 21/02/2016.
 */
public class LinkAcceptedReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() throws RequestException {
		Libersaurus.current.validateContact(sender(), secret());
	}
}
