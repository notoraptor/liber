package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;
import liber.notification.Notification;

public class DeleteContactCommand extends CommandForOnline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.contactLiberaddress};
	}
	@Override
	public boolean checkCommandLine() {
		if (!super.checkCommandLine()) return false;
		String s = get(CommandField.contactLiberaddress);
		return !(s == null || s.isEmpty()) || Notification.bad("Aucun contact spécifié pour la suppression de contact.");
	}
	@Override
	public void execute() {
		Libersaurus.current.features().deleteContact(get(CommandField.contactLiberaddress));
	}
}
