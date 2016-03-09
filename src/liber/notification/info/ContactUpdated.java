package liber.notification.info;

import liber.data.Contact;
import liber.enumeration.ContactData;
import liber.notification.Info;

public class ContactUpdated extends Info<Contact> {
	boolean firstname;
	boolean lastname;
	boolean photo;
	boolean status;
	boolean complete;
	public ContactUpdated(Contact contact, ContactData ... updated) {
		super(contact);
		if(updated.length == 0)
			complete = true;
		else {
			for(ContactData type: updated) {
				switch (type) {
					case firstname:
						firstname = true;
						break;
					case lastname:
						lastname = true;
						break;
					case photo:
						photo = true;
					case status:
						status = true;
					default:
						break;
				}
			}
			int total = 0;
			if(firstname) ++total;
			if(lastname) ++total;
			if(photo) ++total;
			if(status) ++total;
			complete = (total == 4);
		}
	}
	public boolean complete() {return complete;}
	public boolean firstname() {return firstname;}
	public boolean lastname() {return lastname;}
	public boolean photo() {return photo;}
	public boolean status() {return status;}
}
