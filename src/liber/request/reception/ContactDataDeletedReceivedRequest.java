package liber.request.reception;

import liber.Libersaurus;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.request.ReceivedRequest;

/**
 * Created by HPPC on 21/02/2016.
 */
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
