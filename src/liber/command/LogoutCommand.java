package liber.command;

import liber.Libersaurus;

/**
 * Created by HPPC on 21/02/2016.
 */
public class LogoutCommand extends CommandForLoaded {
	@Override
	public void execute() {
		Libersaurus.current.features().logout();
	}
}
