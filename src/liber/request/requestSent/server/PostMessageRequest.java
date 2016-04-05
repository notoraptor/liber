package liber.request.requestSent.server;

import liber.data.OutMessage;
import liber.enumeration.Field;
import liber.request.requestSent.RequestFromUserToLiberserver;
import liber.request.requestSent.RequestName;
import liber.request.requestSent.client.MessageRequest;

public class PostMessageRequest extends RequestFromUserToLiberserver {
	public PostMessageRequest(OutMessage message) throws Exception {
		super(message.liberaddress().liberserver());
		MessageRequest messageRequest = new MessageRequest(message);
		add(Field.username, message.liberaddress().username());
		add(Field.body, messageRequest.encrypt());
		//add(Field.microtime, message.microtime());
		//add(Field.message, message.encodedContent());
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.postMessage;
	}
}
