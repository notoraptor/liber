package liber.card;

import liber.data.Liberaddress;
import liber.data.Message;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by HPPC on 21/02/2016.
 */
public abstract class Links<E extends Message> {
	private HashMap<Liberaddress, E> links;
	public Links() {
		links = new HashMap<>();
	}
	public void add(E link) {
		links.put(link.liberaddress(), link);
	}
	public void remove(E link) {
		links.remove(link.liberaddress());
	}
	public boolean has(E link) {
		return links.containsKey(link.liberaddress());
	}
	public boolean has(Liberaddress liberaddress) {
		return links.containsKey(liberaddress);
	}
	public E get(Liberaddress liberaddress) {
		return links.get(liberaddress);
	}
	public Collection<E> invitations() {
		return links.values();
	}
	public boolean isEmpty() {
		return links.isEmpty();
	}
	public int size() {
		return links.size();
	}
}
