package liber.command;

import liber.enumeration.CommandField;
import liber.notification.Notification;

import java.util.EnumMap;

public abstract class Command {
	private EnumMap<CommandField, String> parameters;
	public Command() {
		parameters = new EnumMap<>(CommandField.class);
	}
	public String get(CommandField key) {
		return parameters.get(key);
	}
	public boolean analyzeCommandLine() {
		return checkCommandLine();
	}
	public boolean checkCommandLine() {
		CommandField[] fields = fields();
		if(fields != null) for(CommandField field: fields) {
			if(!parameters.containsKey(field)) {
				Notification.bad(field.toString() + "missing.");
				return false;
			}
		}
		return true;
	}
	public void put(CommandField field, String value) {
		parameters.put(field, value);
	}
	public boolean run() {
		if (check() && checkCommandLine()) {
			Notification.watch();
			execute();
			return Notification.well();
		}
		return false;
	}
	// À surcharger.
	public boolean check() {
		return true;
	}
	public CommandField[] fields() {
		return null;
	}
	abstract public void execute();
}