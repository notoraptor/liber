package liber.request.requestSent.server;

import liber.enumeration.Field;
import liber.request.requestSent.RequestFromAccountToLiberserver;
import liber.request.requestSent.RequestName;

public class GetNextPostedMessageRequest extends RequestFromAccountToLiberserver {
	public Field[] goodFields() {
		return new Field[]{Field.requestBody};
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.getNextPostedMessage;
	}
}
