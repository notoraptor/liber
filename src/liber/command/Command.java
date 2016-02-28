package liber.command;

import liber.Utils;
import liber.enumeration.CommandField;
import liber.notification.Notification;

import java.util.HashMap;
import java.util.Scanner;

public abstract class Command {
	private Scanner scanner;
	private HashMap<CommandField, String> parameters;
	public Command() {
		parameters = new HashMap<>();
	}
	static public Command getInstance(String commandName) {
		commandName = "liber.command." +
				Character.toUpperCase(commandName.charAt(0))
						+ commandName.substring(1, commandName.length()) + "Command";
		Command command = null;
		if (Utils.classExtends(commandName, Command.class))
			command = (Command) Utils.instanciate(commandName);
		return command;
	}
	public String get(CommandField key) {
		return parameters.get(key);
	}
	public Scanner scanner() {
		return scanner;
	}
	public boolean getCommandLine(Scanner scanner) {
		this.scanner = scanner;
		CommandField[] fields = fields();
		if (fields != null) for (CommandField field : fields) {
			System.out.print("[" + field + "]: ");
			String value = scanner.nextLine().trim();
			parameters.put(field, value);
		}
		return analyzeCommandLine();
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
			if(Notification.well()) return true;
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

/* TODO: Commandes additionnelles pour l'interface en ligne de commande;
* listContacts
* listInlinks
* listOutlinks
* */