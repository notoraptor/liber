package liber.request.requestSent.client;

import liber.data.Contact;
import liber.request.requestSent.RequestName;
import liber.request.requestSent.RequestToLiberaddress;

public class NowOfflineRequest extends RequestToLiberaddress {
	public NowOfflineRequest(Contact contact) {
		super(contact);
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.nowOffline;
	}
}
