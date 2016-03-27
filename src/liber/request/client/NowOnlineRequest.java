package liber.request.client;

import liber.data.Contact;
import liber.request.RequestName;
import liber.request.RequestToLiberaddress;

public class NowOnlineRequest extends RequestToLiberaddress {
	public NowOnlineRequest(Contact contact) {
		super(contact);
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.nowOnline;
	}
}
