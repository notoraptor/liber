package liber.request.client;

import liber.data.User;
import liber.enumeration.Field;
import liber.request.RequestToLiberaddress;

public class GetMessageHashRequest extends RequestToLiberaddress {
	public GetMessageHashRequest(User user, long messageMicrotime) {
		super(user);
		add(Field.microtime, String.valueOf(messageMicrotime));
	}
	@Override
	public Field[] goodFields() {
		return new Field[]{Field.messageHash};
	}
}
