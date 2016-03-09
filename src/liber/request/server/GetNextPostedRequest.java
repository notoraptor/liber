package liber.request.server;

import liber.enumeration.Field;
import liber.request.RequestFromAccountToLiberserver;

public class GetNextPostedRequest extends RequestFromAccountToLiberserver {
	public Field[] goodFields() {
		return new Field[]{Field.sender, Field.body};
	}
}
