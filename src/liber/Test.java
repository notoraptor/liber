package liber;

import liber.command.Command;
import liber.command.LogoutCommand;
import liber.notification.Notification;

import java.util.HashSet;
import java.util.Scanner;

public class Test {
	static private HashSet<String> usedCommands;
	static private HashSet<String> unusedCommands;
	static public void init() {
		usedCommands = new HashSet<>();
		unusedCommands = new HashSet<>();
		unusedCommands.add("acceptInlink");
		unusedCommands.add("cancelOutlink");
		unusedCommands.add("clearHistory");
		unusedCommands.add("createAccount");
		unusedCommands.add("deleteAccount");
		unusedCommands.add("deleteContact");
		unusedCommands.add("deleteInfo");
		unusedCommands.add("login");
		unusedCommands.add("logout");
		unusedCommands.add("newContact");
		unusedCommands.add("newMessage");
		unusedCommands.add("refuseInlink");
		unusedCommands.add("updateInfo");
		unusedCommands.add("validateCreation");
		unusedCommands.add("validateDeletion");
	}
	static public void printCommands() {
		System.out.print("(Unused commands) {");
		boolean written = false;
		for (String command : unusedCommands) {
			if (written) System.out.print(", ");
			else written = true;
			System.out.print(command);
		}
		System.out.println("}.");
		System.out.print("(Used   commands) {");
		written = false;
		for (String command : usedCommands) {
			if (written) System.out.print(", ");
			else written = true;
			System.out.print(command);
		}
		System.out.println("}.");
		System.out.println("(To exit type either) {end, exit}.");
	}
	static public void main(String[] args) {
		init();
		try (
				Libersaurus instance = new Libersaurus()
		) {
			Scanner scanner = new Scanner(System.in);
			printCommands();
			System.out.print("[Votre commande]: ");
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.isEmpty()) {
					Notification.bad("Please enter a command.");
					continue;
				}
				if (line.equals("end") || line.equals("exit")) {
					Command command = new LogoutCommand();
					//Notification.silence();
					//Notification.speak();
					if (command.check() && command.getCommandLine(scanner)) {
						command.execute();
					}
					break;
				}
				Command command = Command.getInstance(line);
				if (command == null) Notification.bad("[Unknown command.]");
				else {
					unusedCommands.remove(line);
					usedCommands.add(line);
					if (command.check()) {
						command.getCommandLine(scanner);
						command.execute();
					} else Notification.bad("This command can't be executed.");
				}
				printCommands();
			}
		} catch (Exception e) {
			System.err.println("Erreur fatale.");
			e.printStackTrace();
		}
	}
}