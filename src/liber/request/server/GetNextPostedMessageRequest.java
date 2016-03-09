package liber.request.server;

import liber.enumeration.Field;
import liber.request.RequestFromAccountToLiberserver;

public class GetNextPostedMessageRequest extends RequestFromAccountToLiberserver {
	public Field[] goodFields() {
		return new Field[]{Field.sender, Field.microtime, Field.message};
	}
}
