package liber.card;

import liber.data.Contact;
import liber.data.Liberaddress;

import java.util.Collection;
import java.util.HashMap;

public class Contacts {
	private HashMap<Liberaddress, Contact> contacts;
	public Contacts() {
		contacts = new HashMap<>();
	}
	public void add(Contact contact) {
		contacts.put(contact.liberaddress(), contact);
	}
	public void remove(Contact contact) {
		contacts.remove(contact.liberaddress());
	}
	public boolean has(Contact contact) {
		return contacts.containsKey(contact.liberaddress());
	}
	public boolean has(Liberaddress liberaddress) {
		return contacts.containsKey(liberaddress);
	}
	public Contact get(Liberaddress liberaddress) {
		return contacts.get(liberaddress);
	}
	public Collection<Contact> list() {
		return contacts.values();
	}
	public boolean isEmpty() {
		return contacts.isEmpty();
	}
	public int size() {
		return contacts.size();
	}
}
