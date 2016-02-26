package liber.request.server;

import liber.enumeration.Field;
import liber.request.RequestFromAccountToLiberserver;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class GetNextPostedMessageRequest extends RequestFromAccountToLiberserver {
	public Field[] goodFields() {
		return new Field[]{Field.sender, Field.microtime, Field.message};
	}
}
