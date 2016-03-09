package liber.notification;

import liber.data.Contact;

/*
* TODO: Il faudrait deux types de notifications: actives et passives.
* actives (suite aux commandes) et passives (suite aux requêtes reçues via le composant serveur.
*/
public class Notification {
	static private Notifier manager = new DefaultNotifier();
	static private boolean silence = false;
	static private int goods = 0;
	static private int bads = 0;
	static public void setManager(Notifier notifier) {
		manager = notifier;
	}
	static public void silence() {
		silence = true;
	}
	static public void speak() {
		silence = false;
	}
	static public void watch() {
		goods = bads = 0;
	}
	static public boolean well() {
		return (goods == 0 && bads == 0 || goods != 0);
	}
	static public boolean good(String notification) {
		if (!silence && manager != null) {
			manager.good(notification);
		}
		++goods;
		return true;
	}
	static public boolean bad(String notification) {
		if (!silence && manager != null) {
			manager.bad(notification);
		}
		++bads;
		return false;
	}
	static public void info(Info info) {
		if(!silence && manager != null) manager.info(info);
	}
	public static void contactDeleted(Contact contact) {
		Notification.bad("Contact " + contact.appellation()
				+ " is no more available. He has deleted either his account or the link with you.");
	}
}
