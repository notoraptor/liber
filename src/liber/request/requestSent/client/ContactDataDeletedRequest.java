package liber.request.requestSent.client;

import liber.data.Contact;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.request.requestSent.RequestName;
import liber.request.requestSent.RequestToLiberaddress;

public class ContactDataDeletedRequest extends RequestToLiberaddress {
	public ContactDataDeletedRequest(Contact contact, ContactData data) {
		super(contact);
		assert data != null;
		add(Field.data, data.toString());
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.contactDataDeleted;
	}
}
