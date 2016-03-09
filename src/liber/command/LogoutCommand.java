package liber.command;

import liber.Libersaurus;

public class LogoutCommand extends CommandForLoaded {
	@Override
	public void execute() {
		Libersaurus.current.features().logout();
	}
}
