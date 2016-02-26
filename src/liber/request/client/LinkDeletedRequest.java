package liber.request.client;

import liber.data.Contact;
import liber.request.RequestToLiberaddress;

/**
 * Created by HPPC on 21/02/2016.
 */
public class LinkDeletedRequest extends RequestToLiberaddress {
	public LinkDeletedRequest(Contact contact) {
		super(contact);
	}
}
