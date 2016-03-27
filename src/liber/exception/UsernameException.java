package liber.exception;

public class UsernameException extends Exception {
	public UsernameException(String un) {
		super("Mauvais nom d'utilisateur: " + un);
	}
}
