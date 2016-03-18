package liber.request.client;

import liber.Libersaurus;
import liber.data.Contact;
import liber.enumeration.Field;
import liber.request.RequestName;
import liber.request.RequestToLiberaddress;

public class NowOnlineRequest extends RequestToLiberaddress {
	public NowOnlineRequest(Contact contact) {
		super(contact);
		add(Field.firstname, Libersaurus.current.account().info().firstname());
		add(Field.lastname, Libersaurus.current.account().info().lastname());
		add(Field.photo, Libersaurus.current.account().info().photo());
		add(Field.status, Libersaurus.current.account().info().status());
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.nowOnline;
	}
}
