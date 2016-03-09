package liber.request.reception;

import liber.Libersaurus;
import liber.request.ReceivedRequest;

public class NowOfflineReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() {
		Libersaurus.current.setContactOffline(sender(), secret());
	}
}
