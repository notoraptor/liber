package liber.card;

import liber.Libersaurus;
import liber.Utils;
import liber.card.textable.*;
import liber.data.*;
import liber.exception.LibercardException;
import liber.notification.Notification;
import liber.security.cvr.CVRInput;
import liber.security.cvr.CVRLineReader;
import liber.security.cvr.CVROutput;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

/* TODO TESTS À FAIRE
Tester une connexion à un compte depuis un ordinateur sur lequel la libercarte n'est pas présente.
	La connexion doit échouer.
Tester une connexion à un compte suivie d'une nouvelle connexion au même compte depuis le même ordinateur.
	La deuxième connexion doit échouer.
Tester:
	Connexion 1 : fermer prématurément le programme (équivalent à Ctrl+C).
	Connexion 2 :
		Moins de 3 secondes après la connexion 1: la connexion doit échouer.
		Au moins 3 secondes après la connexion 1: la connexion doit réussir.
*/
public class Libercard {
	private enum From { CREATE, LOAD }
	static public final From CREATE = From.	CREATE;
	static public final From LOAD = From.LOAD;
	static public final String libercardExtension = ".libercard";
	static public final String libercardFoldername = "libercards";
	static public final String historiesFoldername = "histories";
	public Account account;
	final public String password;
	final public Contacts contacts;
	final public Outlinks outlinks;
	final public Inlinks inlinks;
	final private File file;
	private LibercardLock libercardLock;
	public Libercard(Liberaddress liberaddress, String thePassword, From from) throws Exception {
		password = thePassword;
		contacts = new Contacts();
		outlinks = new Outlinks();
		inlinks = new Inlinks();
		// account maintenant
		if(from == From.CREATE) {
			File directory = new File(Libersaurus.current.getDirectory(), libercardFoldername);
			if (!directory.exists()) {
				if (!directory.mkdir())
					throw new LibercardException("Impossibler de créer le dossier " + libercardFoldername);
			} else if (!directory.isDirectory())
				throw new LibercardException("Impossible de sauver la libercarte vers le chemin \"" + libercardFoldername +
						"\" car ce chemin ne mène pas vers un dossier.");
			file = new File(directory, getFilename(liberaddress));
			account = new Account(liberaddress);
			libercardLock = new LibercardLock(file);
			libercardLock.start();
		} else {
			file = new File(Libersaurus.current.getDirectory(), libercardFoldername + '/' + getFilename(liberaddress));
			if(!file.exists())
				throw new LibercardException("Impossible de trouver la libercarte pour la liber-adresse " + liberaddress);
			if(!file.isFile())
				throw new LibercardException("Le chemin vers la libercarte ne mène pas vers un fichier pour la liber-adresse " + liberaddress);
			libercardLock = new LibercardLock(file);
			libercardLock.start();
			try ( CVRInput cvrInput = new CVRInput(file, password) ) {
				CVRLineReader reader = new CVRLineReader(cvrInput);
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (!line.isEmpty()) {
						fromText(line);
					}
				}
			}
			if (account == null)
				throw new LibercardException("Impossible de repérer un compte dans la libercarte de la liber-adresse " + liberaddress);
			if (!account.liberaddress().equals(liberaddress))
				throw new LibercardException("La liber-adresse spécifiée dans la libercarte ne correspond pas à au compte à charger pour la liber-adresse " + liberaddress);
		}
	}
	public String getPath() {
		return file.getAbsolutePath();
	}
	public void save() throws Exception {
		try {
			try (CVROutput writer = new CVROutput(file, password)) {
				StringBuilder s = new TextableAccount(this, account).toText();
				writer.write(s.toString());
				writer.newLine();
				for (Contact contact : contacts.list()) {
					s = new TextableContact(this, contact).toText();
					writer.write(s.toString());
					writer.newLine();
					for (Message message : contact.messages()) {
						if (message instanceof InMessage)
							s = new TextableInMessage(this, (InMessage) message).toText();
						else
							s = new TextableOutMessage(this, (OutMessage) message).toText();
						writer.write(s.toString());
						writer.newLine();
					}
				}
				for (InMessage invitation : inlinks.invitations()) {
					s = new TextableInlink(this, invitation).toText();
					writer.write(s.toString());
					writer.newLine();
				}
				for (OutMessage invitation : outlinks.invitations()) {
					s = new TextableOutlink(this, invitation).toText();
					writer.write(s.toString());
					writer.newLine();
				}
			}
		} finally {
			System.err.println("Fermeture en cours ...");
			libercardLock.unlock();
			libercardLock.interrupt();
			try {
				libercardLock.join();
			} catch (InterruptedException ignored) {}
		}
		///
		System.err.println("[Libercarte sauvegardee.]");
	}
	public void delete() throws LibercardException {
		if (!file.delete())
			throw new LibercardException("Impossible de supprimer la liber-carte " + file.getAbsolutePath());
		System.err.println("[Liber-carte supprimée.]");
	}
	public void deleteContact(Contact contact) {
		if (contacts.has(contact)) {
			contacts.remove(contact);
			try {
				String historyFilename = contact.saveHistory();
				Notification.good("L'historique de vos échanges avec " +
					contact.appellation() + " a été sauvegardé dans le fichier: " + historyFilename + '\n' +
					"Vous pouvez récupérer ou supprimer ce fichier selon vos besoins.");
			} catch (Exception e) {
				Notification.bad("Impossible de sauvegarder l'historique de vos échanges avec " +
					contact.appellation() + '.');
				e.printStackTrace();
			}
		}
	}
	public LibercardReport report() {
		LibercardReport report = new LibercardReport();
		Iterator<Contact> contactIterator = contacts.list().iterator();
		while (contactIterator.hasNext()) {
			Contact contact = contactIterator.next();
			if(!contact.exists()) {
				contactIterator.remove();
				report.add(contact);
			}
		}
		Iterator<InMessage> inMessageIterator = inlinks.invitations().iterator();
		while (inMessageIterator.hasNext()) {
			InMessage inlink = inMessageIterator.next();
			if(!inlink.sender().exists()) {
				inMessageIterator.remove();
				report.add(inlink);
			}
		}
		Iterator<OutMessage> outMessageIterator = outlinks.invitations().iterator();
		while (outMessageIterator.hasNext()) {
			OutMessage outlink = outMessageIterator.next();
			if(!outlink.recipient().exists()) {
				outMessageIterator.remove();
				report.add(outlink);
			}
		}
		return report;
	}
	private void fromText(String line) throws Exception {
		String[] pieces = line.split("\t");
		if (pieces.length > 1) {
			String name = pieces[0].trim();
			if (!name.isEmpty()) {
				HashMap<String, String> map = new HashMap<>();
				for (int i = 1; i < pieces.length; ++i) {
					String piece = pieces[i];
					String[] parts = piece.split("=", 2);
					if (parts.length != 2) throw new Exception("parts.length != 2");
					String key = parts[0].trim();
					String value = parts[1].trim();
					if (value.isEmpty()) value = null;
					map.put(key, value);
				}
				switch (name) {
					case "account":
						TextableAccount textableAccount = new TextableAccount(this, null);
						textableAccount.check(map);
						account = textableAccount.fromText(map);
						break;
					case "contact":
						TextableContact textableContact = new TextableContact(this, null);
						textableContact.check(map);
						Contact contact = textableContact.fromText(map);
						contacts.add(contact);
						break;
					case "inmessage":
						TextableInMessage textableInMessage = new TextableInMessage(this, null);
						textableInMessage.check(map);
						textableInMessage.fromText(map);
						break;
					case "outmessage":
						TextableOutMessage textableOutMessage = new TextableOutMessage(this, null);
						textableOutMessage.check(map);
						textableOutMessage.fromText(map);
						break;
					case "inlink":
						TextableInlink textableInlink = new TextableInlink(this, null);
						textableInlink.check(map);
						InMessage inlink = textableInlink.fromText(map);
						inlinks.add(inlink);
						break;
					case "outlink":
						TextableOutlink textableOutlink = new TextableOutlink(this, null);
						textableOutlink.check(map);
						OutMessage outlink = textableOutlink.fromText(map);
						outlinks.add(outlink);
						break;
					default:
						break;
				}
			}
		}
	}
	static private String getFilename(Liberaddress liberaddress) throws Exception {
		return Utils.hash(liberaddress.toString()) + libercardExtension;
	}
}