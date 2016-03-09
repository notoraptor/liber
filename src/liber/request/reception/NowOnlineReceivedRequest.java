package liber.request.reception;

import liber.Libersaurus;
import liber.data.Contact;
import liber.data.UserInfo;
import liber.enumeration.Field;
import liber.exception.RequestException;
import liber.request.ReceivedRequest;
import liber.request.Response;

public class NowOnlineReceivedRequest extends ReceivedRequest {
	@Override
	public Field[] needed() {
		return new Field[]{Field.waitingMessages, Field.firstname, Field.lastname, Field.photo, Field.status};
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
	@Override
	public Response respond() throws RequestException {
		manage();
		Response response = new Response();
		Contact contact = Libersaurus.current.getContact(sender());
		response.add(Field.waitingMessages, String.valueOf(contact.countLocationWaitingMessages()));
		response.add(Field.firstname, Libersaurus.current.account().info().firstname());
		response.add(Field.lastname, Libersaurus.current.account().info().lastname());
		response.add(Field.photo, Libersaurus.current.account().info().photo());
		response.add(Field.contactStatus, Libersaurus.current.account().info().status());
		return response;
	}
}
