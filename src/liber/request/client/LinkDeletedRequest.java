package liber.request.client;

import liber.data.Contact;
import liber.request.RequestName;
import liber.request.RequestToLiberaddress;

public class LinkDeletedRequest extends RequestToLiberaddress {
	public LinkDeletedRequest(Contact contact) {
		super(contact);
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.linkDeleted;
	}
}
