package liber.request.reception;

import liber.Libersaurus;
import liber.request.ReceivedRequest;

/**
 * Created by HPPC on 21/02/2016.
 */
public class LinkRefusedReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() {
		Libersaurus.current.deleteOutlink(sender(), secret());
	}
}
