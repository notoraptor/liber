package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;
import liber.notification.Notification;

public class NewMessageCommand extends CommandForOnline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.contact, CommandField.message};
	}
	@Override
	public boolean checkCommandLine() {
		if (!super.checkCommandLine()) return false;
		String s = get(CommandField.contact);
		if (s == null || s.isEmpty()) return Notification.bad("Contact missing for message sending.");
		s = get(CommandField.message);
		return !(s == null || s.isEmpty()) || Notification.bad("Content missing for message sending.");
	}
	@Override
	public void execute() {
		String contact = get(CommandField.contact);
		String message = get(CommandField.message);
		Libersaurus.current.features().newMessage(contact, message);
	}
}
