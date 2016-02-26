package liber.request.client;

import liber.data.User;
import liber.request.RequestToLiberaddress;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class LinkRefusedRequest extends RequestToLiberaddress {
	public LinkRefusedRequest(User user) {
		super(user);
	}
}
