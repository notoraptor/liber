package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;
import liber.notification.Notification;
import liber.request.Response;

public class CreateAccountCommand extends CommandForOffline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.liberserver, CommandField.username, CommandField.password, CommandField.passwordAgain};
	}
	@Override public boolean checkCommandLine() {
		if (!super.checkCommandLine()) return false;
		String s = get(CommandField.liberserver);
		if (s == null || s.isEmpty()) return Notification.bad("Please enter a liberserver.");
		s = get(CommandField.username);
		if (s == null || s.isEmpty()) return Notification.bad("Please enter an username.");
		s = get(CommandField.password);
		if (s == null || s.isEmpty()) return Notification.bad("Please enter a password.");
		String p = get(CommandField.passwordAgain);
		if (p == null || p.isEmpty()) return Notification.bad("Please enter the password again.");
		return s.equals(p) || Notification.bad("You have not entered the same password twice.");
	}
	@Override
	public void execute() {
		Response response = Libersaurus.current.features().createAccount(
				get(CommandField.liberserver), get(CommandField.username), get(CommandField.password)
		);
		/*
		if (response != null) {
			Notification.good("Account created.");
			// Please then use "validateCreation" command to validate the account
		}
		*/
	}
}
