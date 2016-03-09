package liber.request.server;

import liber.data.OutMessage;
import liber.enumeration.Field;
import liber.request.RequestFromUserToLiberserver;

public class PostMessageRequest extends RequestFromUserToLiberserver {
	public PostMessageRequest(OutMessage message) {
		super(message.liberaddress().liberserver());
		add(Field.username, message.liberaddress().username());
		add(Field.microtime, message.microtime());
		add(Field.message, message.encodedContent());
	}
}
