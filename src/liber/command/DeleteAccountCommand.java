package liber.command;

import liber.Libersaurus;
import liber.notification.Notification;
import liber.request.Response;

/**
 * Created by HPPC on 21/02/2016.
 */
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
		Response response = Libersaurus.current.features().deleteAccount();
		/*
		if (response != null) {
			Command validation = new ValidateDeletionCommand(response);
			if (validation.check()) {
				validation.getCommandLine(scanner());
				validation.execute();
			}
		}
		*/
	}
}
