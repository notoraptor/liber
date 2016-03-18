package liber.request.client;

import liber.data.Contact;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.request.RequestName;
import liber.request.RequestToLiberaddress;

public class ContactDataUpdatedRequest extends RequestToLiberaddress {
	public ContactDataUpdatedRequest(Contact contact, ContactData data, String value) {
		super(contact);
		assert data != null && value != null;
		add(Field.data, data.toString());
		add(Field.value, value);
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.contactDataUpdated;
	}
}
