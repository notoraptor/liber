package liber.request.requestSent.server;

import liber.request.requestSent.RequestFromAccountToLiberserver;
import liber.request.requestSent.RequestName;

public class NextPostedReceivedRequest extends RequestFromAccountToLiberserver {
	@Override
	protected RequestName getRequestName() {
		return RequestName.nextPostedReceived;
	}
}
