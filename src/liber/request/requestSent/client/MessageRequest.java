package liber.request.requestSent.client;

import liber.data.OutMessage;
import liber.enumeration.Field;
import liber.request.requestSent.RequestName;
import liber.request.requestSent.RequestToLiberaddress;

public class MessageRequest extends RequestToLiberaddress {
	public MessageRequest(OutMessage message) {
		super(message.recipient());
		add(Field.microtime, message.microtime());
		add(Field.message, message.encodedContent());
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.message;
	}
}
