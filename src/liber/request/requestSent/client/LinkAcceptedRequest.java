package liber.request.requestSent.client;

import liber.data.User;
import liber.request.requestSent.RequestName;
import liber.request.requestSent.RequestToLiberaddress;

public class LinkAcceptedRequest extends RequestToLiberaddress {
	public LinkAcceptedRequest(User user) {
		super(user);
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.linkAccepted;
	}
}
