package liber.card.textable;

import liber.card.Libercard;
import liber.data.Contact;
import liber.data.Liberaddress;
import liber.data.UserInfo;

import java.util.HashMap;

public class TextableContact extends Textable<Contact> {
	static private String liberaddress = "liberaddress";
	static private String secret = "secret";
	static private String firstname = "firstname";
	static private String lastname = "lastname";
	static private String photo = "photo";
	static private String status = "status";
	public TextableContact(Libercard libercard, Contact contact) {
		super(libercard, contact);
	}
	@Override
	public String name() {
		return "contact";
	}
	@Override
	public String[] fields() {
		return new String[]{liberaddress, secret, firstname, lastname, photo, status};
	}
	@Override
	public void toText(HashMap<String, String> map) {
		map.put(liberaddress, get().liberaddress().toString());
		map.put(secret, get().secret());
		map.put(firstname, get().info().firstname());
		map.put(lastname, get().info().lastname());
		map.put(photo, get().info().photo());
		map.put(status, get().info().status());
	}
	@Override
	public Contact fromText(HashMap<String, String> map) throws Exception {
		if (map.get(secret) == null) throw new Exception();
		Contact contact = new Contact(new Liberaddress(map.get(liberaddress)), map.get(secret));
		contact.update(new UserInfo(map.get(firstname), map.get(lastname), map.get(photo), map.get(status)));
		return contact;
	}
}
