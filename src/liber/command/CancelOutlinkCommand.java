package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;

public class CancelOutlinkCommand extends CommandForOnline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.userLiberaddress};
	}
	@Override
	public void execute() {
		Libersaurus.current.features().cancelOutLink(get(CommandField.userLiberaddress));
	}
}
