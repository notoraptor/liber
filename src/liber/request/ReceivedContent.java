package liber.request;

import liber.enumeration.Field;

import java.util.HashMap;

/**
 * Created by HPPC on 21/02/2016.
 */
public class ReceivedContent {
	private HashMap<String, String> content;
	public ReceivedContent() {
		content = new HashMap<>();
	}
	public void put(String key, String value) {
		content.put(key, value);
	}
	public boolean isEmpty() {
		return content.isEmpty();
	}
	public boolean has(Field field) {
		return content.containsKey(field.toString());
	}
	public String get(Field field) {
		return content.get(field.toString());
	}
	public String request() {
		return get(Field.request);
	}
	public String sender() {
		return get(Field.sender);
	}
	public String recipient() {
		return get(Field.recipient);
	}
	public String secret() {
		return get(Field.secret);
	}
}
