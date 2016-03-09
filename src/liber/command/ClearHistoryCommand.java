package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;

public class ClearHistoryCommand extends CommandForOnline {
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.contactLiberaddress};
	}
	@Override
	public void execute() {
		Libersaurus.current.features().clearHistory(get(CommandField.contactLiberaddress));
	}
}
