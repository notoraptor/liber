package liber.request;

import liber.Libersaurus;
import liber.recipient.Liberserver;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public abstract class RequestFromUserToLiberserver extends RequestToLiberserver {
	public RequestFromUserToLiberserver(Liberserver liberserver) {
		super(Libersaurus.current.account().liberaddress(), liberserver);
	}
}
