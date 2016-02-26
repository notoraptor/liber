package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;
import liber.notification.Notification;

/**
 * Created by HPPC on 21/02/2016.
 */
public class NewMessageCommand extends CommandForOnline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.contact, CommandField.message};
	}
	@Override
	public boolean checkCommandLine() {
		if(!super.checkCommandLine()) return false;
		String s = get(CommandField.contact);
		if(s == null || s.isEmpty()) return Notification.bad("Contact missing for message sending.");
		s = get(CommandField.message);
		if(s == null || s.isEmpty()) return Notification.bad("Content missing for message sending.");
		return true;
	}
	@Override
	public void execute() {
		String contact = get(CommandField.contact);
		String message = get(CommandField.message);
		Libersaurus.current.features().newMessage(contact, message);
	}
}
