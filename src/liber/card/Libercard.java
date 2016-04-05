package liber.card;

import liber.Libersaurus;
import liber.Utils;
import liber.card.textable.*;
import liber.data.*;
import liber.exception.LibercardException;
import liber.notification.Notification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;

public class Libercard {
	static public final String libercardFoldername = "libercards";
	static public final String historiesFoldername = "histories";
	public Account account;
	public Contacts contacts;
	public Outlinks outlinks;
	public Inlinks inlinks;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	public Libercard() {
		contacts = new Contacts();
		outlinks = new Outlinks();
		inlinks = new Inlinks();
	}
	public Libercard(Liberaddress liberaddress) throws Exception {
		account = new Account(liberaddress);
		contacts = new Contacts();
		outlinks = new Outlinks();
		inlinks = new Inlinks();
	}
	static public Libercard load(Liberaddress accountLiberaddress) throws Exception {
		Libercard libercard = null;
		File file = new File(Libersaurus.current.getDirectory(), libercardFoldername + '/' + getFilename(accountLiberaddress));
		if (file.exists() && file.isFile()) {
			libercard = new Libercard();
			BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty()) {
					libercard.fromText(line);
				}
			}
			reader.close();
			if (libercard.account == null) {
				throw new Exception("No account defined in libercard file.");
			} else if (!libercard.account.liberaddress().equals(accountLiberaddress)) {
				throw new Exception("liber.Liberaddress specified in libercard file does not match liberaddress ot account to be opened.");
			}
		}
		return libercard;
	}
	static private String getFilename(Liberaddress liberaddress) throws Exception {
		return Utils.hash(liberaddress.toString()) + ".libercard";
	}
	public void save() throws Exception {
		File directory = new File(Libersaurus.current.getDirectory(), libercardFoldername);
		if (!directory.exists()) {
			if (!directory.mkdir())
				throw new Exception("Unable to create \"" + libercardFoldername + "\" directory.");
		} else if (!directory.isDirectory())
			throw new Exception("Unable to use \"" + libercardFoldername + "\" as directory (it is a file) to store libercards.");
		File filename = new File(directory, getFilename(account.liberaddress()));
		///
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename.getAbsolutePath()));
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
		writer.close();
		///
		System.err.println("[Libercarte sauvegardee.]");
	}
	public void delete() throws LibercardException {
		try {
			File file = new File(Libersaurus.current.getDirectory(), libercardFoldername + '/' + getFilename(account.liberaddress()));
			if (file.exists() && file.isFile()) {
				if (!file.delete())
					throw new Exception("Unable to delete libercard \"" + file.getAbsolutePath() + "\".");
				System.err.println("[liber.Libercard deleted.]");
			}
		} catch (Exception e) {
			throw new LibercardException(e);
		}
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
}