package liber.command;

import liber.Libersaurus;
import liber.notification.Notification;

public abstract class CommandForLoaded extends Command {
	public CommandForLoaded() {
		super();
	}
	@Override
	public boolean check() {
		if (!Libersaurus.current.loaded()) {
			Notification.bad("No account loaded.");
			return false;
		}
		return true;
	}
}
