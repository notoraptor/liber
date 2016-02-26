package liber.request.reception;

import liber.Libersaurus;
import liber.request.ReceivedRequest;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class NowOfflineReceivedRequest extends ReceivedRequest {
	@Override
	public void manage() {
		Libersaurus.current.setContactOffline(sender(), secret());
	}
}
