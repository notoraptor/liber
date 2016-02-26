package liber.request.client;

import liber.data.Contact;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.request.RequestToLiberaddress;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class ContactDataDeletedRequest extends RequestToLiberaddress {
	public ContactDataDeletedRequest(Contact contact, ContactData data) {
		super(contact);
		assert data != null;
		add(Field.data, data.toString());
	}
}
