package liber.command;

import liber.Libersaurus;
import liber.notification.Notification;

public class DeleteAccountCommand extends CommandForLoaded {
	@Override
	public boolean check() {
		if (!super.check()) return false;
		if (Libersaurus.current.account().toDelete()) {
			Notification.bad("Loaded account must already be deleted. Use \"validateDeletion\" command.");
			return false;
		}
		return true;
	}
	@Override
	public void execute() {
		Libersaurus.current.features().deleteAccount();
	}
}
