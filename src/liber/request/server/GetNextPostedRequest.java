package liber.request.server;

import liber.enumeration.Field;
import liber.request.RequestFromAccountToLiberserver;
import liber.request.RequestName;

public class GetNextPostedRequest extends RequestFromAccountToLiberserver {
	public Field[] goodFields() {
		return new Field[]{Field.sender, Field.body};
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.getNextPosted;
	}
}
