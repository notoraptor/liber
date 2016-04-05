package liber.request.requestSent.server;

import liber.enumeration.Field;
import liber.request.requestSent.RequestFromUserToLiberserver;
import liber.request.requestSent.RequestName;
import liber.request.requestSent.RequestToLiberaddress;

public class PostLaterRequest extends RequestFromUserToLiberserver {
	public PostLaterRequest(RequestToLiberaddress request) throws Exception {
		super(request.recipientLiberserver());
		add(Field.username, request.recipientUsername());
		add(Field.body, request.encrypt());
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.postLater;
	}
}
