package liber.command;

import liber.Libersaurus;
import liber.notification.Notification;
import liber.enumeration.CommandField;
import liber.enumeration.ContactData;

public class UpdateInfoCommand extends CommandForOnline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.data, CommandField.value};
	}
	@Override
	public boolean checkCommandLine() {
		if(!super.checkCommandLine()) return false;
		String s = get(CommandField.data);
		if(s == null || s.isEmpty()) return Notification.bad("Type of data to be updated is missing.");
		try {
			ContactData.valueOf(s);
		} catch(Exception e) {
			return Notification.bad("Unknown \"data\" type. Possible values: firstname, lastname, photo, status.");
		}
		return true;
	}
	@Override
	public void execute() {
		ContactData data = ContactData.valueOf(get(CommandField.data));
		String value = get(CommandField.value);
		if(value != null) value = value.trim();
		Libersaurus.current.features().updateInfo(data, value);
	}
}
