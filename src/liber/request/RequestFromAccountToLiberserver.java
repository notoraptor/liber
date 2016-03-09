package liber.request;

import liber.Libersaurus;
import liber.enumeration.Field;

public abstract class RequestFromAccountToLiberserver extends RequestToLiberserver {
	public RequestFromAccountToLiberserver() {
		super(Libersaurus.current.account().liberaddress(), Libersaurus.current.account().liberserver());
		add(Field.username, Libersaurus.current.account().username());
		add(Field.password, Libersaurus.current.password());
	}
}
