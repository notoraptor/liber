package liber.request.client;

import liber.data.User;
import liber.request.RequestName;
import liber.request.RequestToLiberaddress;

public class LinkRefusedRequest extends RequestToLiberaddress {
	public LinkRefusedRequest(User user) {
		super(user);
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.linkRefused;
	}
}
