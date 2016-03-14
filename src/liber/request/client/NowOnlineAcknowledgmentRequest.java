package liber.request.client;

import liber.Libersaurus;
import liber.data.Contact;
import liber.enumeration.Field;
import liber.request.RequestToLiberaddress;

public class NowOnlineAcknowledgmentRequest extends RequestToLiberaddress {
	public NowOnlineAcknowledgmentRequest(Contact contact) {
		super(contact);
		add(Field.waitingMessages, String.valueOf(contact.countLocationWaitingMessages()));
		add(Field.firstname, Libersaurus.current.account().info().firstname());
		add(Field.lastname, Libersaurus.current.account().info().lastname());
		add(Field.photo, Libersaurus.current.account().info().photo());
		add(Field.status, Libersaurus.current.account().info().status());
	}
}