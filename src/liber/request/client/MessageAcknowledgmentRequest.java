package liber.request.client;

import liber.data.User;
import liber.enumeration.Field;
import liber.request.RequestName;
import liber.request.RequestToLiberaddress;

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
