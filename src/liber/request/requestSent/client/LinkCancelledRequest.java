package liber.request.requestSent.client;

import liber.data.User;
import liber.request.requestSent.RequestName;
import liber.request.requestSent.RequestToLiberaddress;

public class LinkCancelledRequest extends RequestToLiberaddress {
	public LinkCancelledRequest(User user) {
		super(user);
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.linkCancelled;
	}
}
