package liber.request.requestReceived.reception;

import liber.Libersaurus;
import liber.request.requestReceived.ReceivedRequest;

public class NowOfflineReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() {
		Libersaurus.current.setContactOffline(sender(), secret());
	}
}
