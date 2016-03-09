package liber.request.client;

import liber.data.User;
import liber.request.RequestToLiberaddress;

public class LinkRefusedRequest extends RequestToLiberaddress {
	public LinkRefusedRequest(User user) {
		super(user);
	}
}
