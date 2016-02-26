package liber.command;

import liber.Libersaurus;
import liber.notification.Notification;

/**
 * Created by HPPC on 21/02/2016.
 */
public abstract class CommandForOnline extends Command {
	public CommandForOnline() {
		super();
	}
	@Override
	public boolean check() {
		if (!Libersaurus.current.online()) {
			Notification.bad("No valid account online. Please login (or create account) first.");
			return false;
		}
		return true;
	}
}
