package liber.request.client;

import liber.data.Contact;
import liber.request.RequestName;
import liber.request.RequestToLiberaddress;

public class NowOfflineRequest extends RequestToLiberaddress {
	public NowOfflineRequest(Contact contact) {
		super(contact);
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.nowOffline;
	}
}
