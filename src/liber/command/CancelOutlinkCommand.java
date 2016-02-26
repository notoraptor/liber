package liber.command;

import liber.Libersaurus;
import liber.enumeration.CommandField;

/**
 * Created by HPPC on 21/02/2016.
 */
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
