package liber.request.server;

import liber.data.Liberaddress;
import liber.data.OutMessage;
import liber.enumeration.Field;
import liber.request.RequestFromUserToLiberserver;

/**
 * Created by HPPC on 21/02/2016.
 */
public class CheckPostedMessageRequest extends RequestFromUserToLiberserver {
	public CheckPostedMessageRequest(Liberaddress recipient, long microtime) {
		super(recipient.liberserver());
		add(Field.username, recipient.username());
		add(Field.microtime, microtime);
	}
	public CheckPostedMessageRequest(OutMessage message) {
		this(message.liberaddress(), message.microtime());
	}
}
