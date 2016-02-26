package liber.command;

import liber.Libersaurus;
import liber.notification.Notification;

/**
 * Created by HPPC on 21/02/2016.
 */
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
