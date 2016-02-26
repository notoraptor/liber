package liber.request.client;

import liber.data.Contact;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.request.RequestToLiberaddress;

/**
 * Created by HPPC on 21/02/2016.
 */ // liber.RequestToLiberaddress.
public class ContactDataUpdatedRequest extends RequestToLiberaddress {
	public ContactDataUpdatedRequest(Contact contact, ContactData data, String value) {
		super(contact);
		assert data != null && value != null;
		add(Field.data, data.toString());
		add(Field.value, value);
	}
}
