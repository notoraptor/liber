package liber.request.client;

import liber.data.Contact;
import liber.enumeration.Field;
import liber.request.RequestName;
import liber.request.RequestToLiberaddress;

public class NowOnlineAcknowledgmentRequest extends RequestToLiberaddress {
	public NowOnlineAcknowledgmentRequest(Contact contact) {
		super(contact);
		add(Field.waitingMessages, String.valueOf(contact.countLocationWaitingMessages()));
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.nowOnlineAcknowledgment;
	}
}
