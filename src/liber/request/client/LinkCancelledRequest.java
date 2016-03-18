package liber.request.client;

import liber.data.User;
import liber.request.RequestName;
import liber.request.RequestToLiberaddress;

public class LinkCancelledRequest extends RequestToLiberaddress {
	public LinkCancelledRequest(User user) {
		super(user);
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.linkCancelled;
	}
}
