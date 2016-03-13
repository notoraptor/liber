package liber.request.reception;

import liber.Libersaurus;
import liber.data.UserInfo;
import liber.enumeration.Field;
import liber.exception.RequestException;
import liber.request.ReceivedRequest;

public class NowOnlineReceivedRequest extends ReceivedRequest {
	@Override
	public Field[] needed() {
		return new Field[]{Field.firstname, Field.lastname, Field.photo, Field.status};
	}
	@Override
	public void manage() throws RequestException {
		UserInfo info = new UserInfo(
			get(Field.firstname),
			get(Field.lastname),
			get(Field.photo),
			get(Field.status)
		);
		Libersaurus.current.setContactOnline(sender(), secret(), info);
	}
}
