package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;
import liber.notification.Notification;

/**
 * Created by HPPC on 21/02/2016.
 */
public class NewContactCommand extends CommandForOnline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.user, CommandField.invitation};
	}
	@Override public boolean checkCommandLine() {
		if (!super.checkCommandLine()) return false;
		String s = get(CommandField.user);
		if (s == null || s.isEmpty()) return Notification.bad("Veuillez indiquer la liber-adresse du contact.");
		s = get(CommandField.invitation);
		return !(s == null || s.isEmpty()) || Notification.bad("Veuillez écrire un message d'invitation!");
	}
	@Override
	public void execute() {
		Libersaurus.current.features().newContact(get(CommandField.user), get(CommandField.invitation));
	}
}
