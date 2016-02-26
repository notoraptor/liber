package liber.request.server;

import liber.data.Liberaddress;
import liber.data.OutMessage;
import liber.enumeration.Field;
import liber.request.RequestFromUserToLiberserver;

/**
 * Created by HPPC on 21/02/2016.
 */ // liber.RequestFromUserToLiberserver
public class PostMessageRequest extends RequestFromUserToLiberserver {
	public PostMessageRequest(Liberaddress recipient, long microtime, String content) {
		super(recipient.liberserver());
		add(Field.username, recipient.username());
		add(Field.microtime, microtime);
		add(Field.message, content);
	}
	public PostMessageRequest(OutMessage message) {
		this(message.liberaddress(), message.microtime(), message.encodedContent());
	}
}
