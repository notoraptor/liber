package liber.request.client;

import liber.data.User;
import liber.request.RequestName;
import liber.request.RequestToLiberaddress;

public class LinkAcceptedRequest extends RequestToLiberaddress {
	public LinkAcceptedRequest(User user) {
		super(user);
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.linkAccepted;
	}
}
