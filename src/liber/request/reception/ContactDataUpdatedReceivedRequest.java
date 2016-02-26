package liber.request.reception;

import liber.Libersaurus;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.request.ReceivedRequest;

/**
 * Created by HPPC on 21/02/2016.
 */
public class ContactDataUpdatedReceivedRequest extends ReceivedRequest {
	@Override
	public Field[] needed() {
		return new Field[]{Field.data, Field.value};
	}
	@Override
	public void manage() {
		String dataName = get(Field.data);
		String dataValue = get(Field.value);
		try {
			ContactData dataType = ContactData.valueOf(dataName);
			Libersaurus.current.updateContactData(sender(), secret(), dataType, dataValue);
		} catch (Exception ignored) {}
	}
}
