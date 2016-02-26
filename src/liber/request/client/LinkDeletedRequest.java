package liber.request.client;

import liber.data.Contact;
import liber.request.RequestToLiberaddress;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class LinkDeletedRequest extends RequestToLiberaddress {
	public LinkDeletedRequest(Contact contact) {
		super(contact);
	}
}
