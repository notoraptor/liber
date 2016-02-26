package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;
import liber.enumeration.ContactData;
import liber.notification.Notification;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class DeleteInfoCommand extends CommandForOnline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.data};
	}
	@Override
	public void execute() {
		try {
			ContactData data = ContactData.valueOf(get(CommandField.data));
			Libersaurus.current.features().deleteInfo(data);
		} catch (Exception e) {
			Notification.bad("Unknown \"data\". Possible values: firstname, lastname, photo, status.");
		}
	}
}
