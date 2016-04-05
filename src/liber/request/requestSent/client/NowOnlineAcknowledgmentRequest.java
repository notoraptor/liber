package liber.request.requestSent.client;

import liber.data.Contact;
import liber.enumeration.Field;
import liber.request.requestSent.RequestName;
import liber.request.requestSent.RequestToLiberaddress;

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
