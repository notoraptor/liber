package liber.request.client;

import liber.data.User;
import liber.enumeration.Field;
import liber.request.RequestToLiberaddress;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class MessageAcknowledgmentRequest extends RequestToLiberaddress {
	public MessageAcknowledgmentRequest(User user, long messageMicrotime) {
		super(user);
		add(Field.microtime, String.valueOf(messageMicrotime));
	}
}
