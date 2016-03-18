package liber.request.server;

import liber.request.RequestFromAccountToLiberserver;
import liber.request.RequestName;

public class NextPostedMessageReceivedRequest extends RequestFromAccountToLiberserver {
	@Override
	protected RequestName getRequestName() {
		return RequestName.nextPostedMessageReceived;
	}
}
