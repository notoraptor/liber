package liber.request;

import liber.Libersaurus;
import liber.recipient.Liberserver;

public abstract class RequestFromUserToLiberserver extends RequestToLiberserver {
	public RequestFromUserToLiberserver(Liberserver liberserver) {
		super(Libersaurus.current.account().liberaddress(), liberserver);
	}
}
