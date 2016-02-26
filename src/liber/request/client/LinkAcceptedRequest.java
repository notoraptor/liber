package liber.request.client;

import liber.data.User;
import liber.request.RequestToLiberaddress;

/**
 * Created by HPPC on 21/02/2016.
 */
public class LinkAcceptedRequest extends RequestToLiberaddress {
	public LinkAcceptedRequest(User user) {
		super(user);
	}
}
