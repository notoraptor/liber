package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;
import liber.notification.Notification;

public class RefuseInlinkCommand extends CommandForOnline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.userLiberaddress};
	}
	@Override public boolean checkCommandLine() {
		if (!super.checkCommandLine()) return false;
		String s = get(CommandField.userLiberaddress);
		return !(s == null || s.isEmpty()) || Notification.bad("No user liberaddress specified to refuse inlink.");
	}
	@Override
	public void execute() {
		Libersaurus.current.features().refuseInlink(get(CommandField.userLiberaddress));
	}
}
