package liber.command;

import liber.Libersaurus;
import liber.notification.Notification;

/**
 * Created by HPPC on 21/02/2016.
 */
public abstract class CommandForOffline extends Command {
	public CommandForOffline() {
		super();
	}
	@Override
	public boolean check() {
		if (Libersaurus.current.loaded()) {
			Notification.bad("An account is already loaded and online.");
			return false;
		}
		return true;
	}
}
