package liber.notification.info;

import liber.data.Contact;
import liber.notification.Info;

/*
* TODO: Mettre en place une option "bloquer le contact".
*/
public class ContactDeleted extends Info<Contact> {
	public ContactDeleted(Contact contact) {
		super(contact);
	}
}
