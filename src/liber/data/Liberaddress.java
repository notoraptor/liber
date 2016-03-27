package liber.data;

import liber.Utils;
import liber.notification.Notification;
import liber.exception.RecipientAddressException;
import liber.exception.UsernameException;
import liber.recipient.Liberserver;

public class Liberaddress {
	private Liberserver liberserver;
	private String username;
	private String liberaddress;
	public Liberaddress(String liberserver, String username) throws UsernameException, RecipientAddressException {
		// Suppression des "/" en fin d'adresse.
		int last = liberserver.length()-1;
		int first;
		for(first = last; first >= 0 && liberserver.charAt(first) == '/'; --first);
		++first;
		if(last - first + 1 > 0)
			liberserver = liberserver.substring(0, first);
		if (Utils.usernameIsInvalid(username)) throw new UsernameException(username);
		if (Utils.urlIsInvalid(liberserver)) liberserver = "http://" + liberserver;
		if (liberserver.endsWith(Liberserver.end))
			liberserver = liberserver.substring(0, liberserver.length() - Liberserver.end.length());
		this.liberserver = new Liberserver(liberserver + Liberserver.end);
		this.username = username;
		this.liberaddress = liberserver + '/' + username;
	}
	public Liberaddress(String liberaddress) throws UsernameException, RecipientAddressException {
		if (Utils.urlIsInvalid(liberaddress)) liberaddress = "http://" + liberaddress;
		int indexOfSeparator = liberaddress.lastIndexOf('/');
		this.liberserver = new Liberserver(liberaddress.substring(0, indexOfSeparator) + Liberserver.end);
		this.username = liberaddress.substring(indexOfSeparator + 1);
		this.liberaddress = liberaddress;
		if (Utils.usernameIsInvalid(username)) throw new UsernameException(username);
	}
	static public Liberaddress build(String liberaddress) {
		Liberaddress la = null;
		try {
			la = new Liberaddress(liberaddress);
		} catch (UsernameException e) {
			Notification.bad("Mauvais nom d'utilisateur.");
		} catch (RecipientAddressException e) {
			Notification.bad("Mauvaise adresse de liber-serveur.");
		}
		return la;
	}
	static public Liberaddress build(String liberserver, String username) {
		Liberaddress la = null;
		try {
			la = new Liberaddress(liberserver, username);
		} catch (UsernameException e) {
			Notification.bad("Bad username");
		} catch (RecipientAddressException e) {
			Notification.bad("Bad user liberserver.");
		}
		return la;
	}
	public Liberserver liberserver() {
		return liberserver;
	}
	public String username() {
		return username;
	}
	@Override
	public String toString() {
		return liberaddress;
	}
	@Override
	public int hashCode() {
		return liberaddress.hashCode();
	}
	@Override
	public boolean equals(Object other) {
		return other != null
				&& (other instanceof Liberaddress || other instanceof CharSequence)
				&& liberaddress.equals(other.toString());
	}
}
