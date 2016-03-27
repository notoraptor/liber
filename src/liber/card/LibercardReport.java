package liber.card;

import liber.data.Contact;
import liber.data.InMessage;
import liber.data.OutMessage;

import java.util.HashMap;
import java.util.LinkedList;

public class LibercardReport {
	private HashMap<Contact, String> removedContactsHistories;
	private LinkedList<Contact> removedContacts;
	private LinkedList<InMessage> removedInlinks;
	private LinkedList<OutMessage> removedOutlinks;
	public LibercardReport() {
		removedContactsHistories = new HashMap<>();
		removedContacts = new LinkedList<>();
		removedInlinks = new LinkedList<>();
		removedOutlinks = new LinkedList<>();
	}
	public void add(Contact contact) {
		removedContacts.add(contact);
		try {
			String historyFilename = contact.saveHistory();
			removedContactsHistories.put(contact, historyFilename);
		} catch (Exception ignored) {}
	}
	public void add(InMessage inlink) {
		removedInlinks.add(inlink);
	}
	public void add(OutMessage outlink) {
		removedOutlinks.add(outlink);
	}
	public boolean isEmpty() {
		return removedContacts.isEmpty() && removedInlinks.isEmpty() && removedOutlinks.isEmpty();
	}
	public boolean historySaved(Contact contact) {
		return removedContactsHistories.containsKey(contact);
	}
	public String historyFilename(Contact contact) {
		return removedContactsHistories.get(contact);
	}
	public LinkedList<Contact> contacts() {
		return removedContacts;
	}
	public LinkedList<InMessage> inlinks() {
		return removedInlinks;
	}
	public LinkedList<OutMessage> outlinks() {
		return removedOutlinks;
	}
}
