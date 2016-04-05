package liber.request.requestSent.client;

import liber.data.User;
import liber.enumeration.Field;
import liber.request.requestSent.RequestName;
import liber.request.requestSent.RequestToLiberaddress;

public class MessageAcknowledgmentRequest extends RequestToLiberaddress {
	public MessageAcknowledgmentRequest(User user, long messageMicrotime) {
		super(user);
		add(Field.microtime, String.valueOf(messageMicrotime));
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.messageAcknowledgment;
	}
}
