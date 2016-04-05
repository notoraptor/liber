package liber.request.requestReceived.reception;

import liber.Libersaurus;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.request.requestReceived.ReceivedRequest;

public class ContactDataDeletedReceivedRequest extends ReceivedRequest {
	@Override
	public Field[] needed() {
		return new Field[]{Field.data};
	}
	@Override
	public void manage() {
		String dataName = get(Field.data);
		try {
			ContactData dataType = ContactData.valueOf(dataName);
			Libersaurus.current.deleteContactData(sender(), secret(), dataType);
		} catch (Exception ignored) {
		}
	}
}
