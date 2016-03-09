package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;
import liber.notification.Notification;

public class AcceptInlinkCommand extends CommandForOnline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.userLiberaddress};
	}
	@Override public boolean checkCommandLine() {
		if (!super.checkCommandLine()) return false;
		String s = get(CommandField.userLiberaddress);
		return !(s == null || s.isEmpty()) || Notification.bad("No user liberaddress specified to accept inlink.");
	}
	@Override
	public void execute() {
		Libersaurus.current.features().acceptInlink(get(CommandField.userLiberaddress));
	}
}
