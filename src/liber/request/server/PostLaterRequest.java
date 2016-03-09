package liber.request.server;

import liber.Utils;
import liber.enumeration.Field;
import liber.request.RequestFromUserToLiberserver;
import liber.request.RequestToLiberaddress;

public class PostLaterRequest extends RequestFromUserToLiberserver {
	public PostLaterRequest(RequestToLiberaddress request) {
		super(request.recipientLiberserver());
		add(Field.username, request.recipientUsername());
		add(Field.body, Utils.encode(request.toString()));
	}
}
