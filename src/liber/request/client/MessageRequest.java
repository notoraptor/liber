package liber.request.client;

import liber.data.OutMessage;
import liber.enumeration.Field;
import liber.request.RequestToLiberaddress;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class MessageRequest extends RequestToLiberaddress {
	public MessageRequest(OutMessage message) {
		super(message.recipient());
		add(Field.microtime, message.microtime());
		add(Field.message, message.encodedContent());
	}
}
