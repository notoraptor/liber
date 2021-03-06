package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;
import liber.notification.Notification;

public class LoginCommand extends CommandForOffline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.liberaddress, CommandField.password};
	}
	@Override
	public boolean checkCommandLine() {
		if (!super.checkCommandLine()) return false;
		String s = get(CommandField.liberaddress);
		if (s == null || s.isEmpty()) return Notification.bad("Veuillez entrer une liber-adresse.");
		s = get(CommandField.password);
		return !(s == null || s.isEmpty()) || Notification.bad("Veuillez entrer un mot de passe.");
	}
	@Override
	public void execute() {
		Libersaurus.current.features().login(get(CommandField.liberaddress), get(CommandField.password));
	}
}
