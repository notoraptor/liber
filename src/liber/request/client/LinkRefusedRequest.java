package liber.request.client;

import liber.data.User;
import liber.request.RequestToLiberaddress;

/**
 * Created by HPPC on 21/02/2016.
 */
public class LinkRefusedRequest extends RequestToLiberaddress {
	public LinkRefusedRequest(User user) {
		super(user);
	}
}
