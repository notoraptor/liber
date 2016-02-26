package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;
import liber.notification.Notification;

/**
 * Created by HPPC on 21/02/2016.
 */
public class DeleteContactCommand extends CommandForOnline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.contactLiberaddress};
	}
	@Override
	public boolean checkCommandLine() {
		if(!super.checkCommandLine()) return false;
		String s = get(CommandField.contactLiberaddress);
		if(s == null || s.isEmpty()) return Notification.bad("Aucun contact spécifié pour la suppression de contact.");
		return true;
	}
	@Override
	public void execute() {
		Libersaurus.current.features().deleteContact(get(CommandField.contactLiberaddress));
	}
}
