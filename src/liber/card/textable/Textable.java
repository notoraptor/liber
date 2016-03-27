package liber.card.textable;

import liber.Utils;
import liber.card.Libercard;

import java.util.HashMap;

abstract class Textable<T> {
	private T component;
	private Libercard libercard;
	public Textable(Libercard libercard, T element) {
		component = element;
		this.libercard = libercard;
	}
	public T get() {
		return component;
	}
	public Libercard libercard() {
		return libercard;
	}
	public void check(HashMap<String, String> map) throws Exception {
		for (String field : fields())
			if (!map.containsKey(field)) {
				throw new Exception("Error while loading " + name() + " from libercard: missing field " + field + '.');
			}
	}
	public StringBuilder toText() {
		StringBuilder s = new StringBuilder();
		s.append(name()).append('\t');
		HashMap<String, String> map = new HashMap<>();
		toText(map);
		Utils.implode(map, s);
		return s;
	}
	abstract public String name();
	abstract public String[] fields();
	abstract public void toText(HashMap<String, String> map);
	abstract public T fromText(HashMap<String, String> map) throws Exception;
}
