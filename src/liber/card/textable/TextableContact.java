package liber.card.textable;

import liber.Utils;
import liber.card.Libercard;
import liber.data.Contact;
import liber.data.Liberaddress;
import liber.data.UserInfo;

import java.util.HashMap;

public class TextableContact extends Textable<Contact> {
	static private final String liberaddress = "liberaddress";
	static private final String secret = "secret";
	static private final String firstname = "firstname";
	static private final String lastname = "lastname";
	static private final String photo = "photo";
	static private final String status = "status";
	static private final String accountPhotoSent = "accountPhotoSent";
	static private final String accountFirstnameSent = "accountFirstnameSent";
	static private final String accountLastnameSent = "accountLastnameSent";
	static private final String accountStatusSent = "accountStatusSent";
	static private final String publicKey = "publicKey";
	static private final String ignored = "ignored";
	public TextableContact(Libercard libercard, Contact contact) {
		super(libercard, contact);
	}
	@Override
	public String name() {
		return "contact";
	}
	@Override
	public String[] fields() {
		return new String[] {
			liberaddress, secret, firstname, lastname, photo, status,
			accountPhotoSent, accountFirstnameSent, accountLastnameSent, accountStatusSent,
			ignored
		};
	}
	@Override
	public void toText(HashMap<String, String> map) {
		map.put(liberaddress, get().liberaddress().toString());
		map.put(secret, get().secret());
		map.put(firstname, get().info().firstname());
		map.put(lastname, get().info().lastname());
		map.put(photo, get().info().photo());
		map.put(status, get().info().status());
		map.put(accountFirstnameSent, String.valueOf(get().accountFirstnameSent()));
		map.put(accountLastnameSent, String.valueOf(get().accountLastnameSent()));
		map.put(accountPhotoSent, String.valueOf(get().accountPhotoSent()));
		map.put(accountStatusSent, String.valueOf(get().accountStatusSent()));
		map.put(ignored, String.valueOf(get().isIgnored()));
		map.put(publicKey, Utils.encodeString(get().encryption().publicKeyToString()));
	}
	@Override
	public Contact fromText(HashMap<String, String> map) throws Exception {
		if (map.get(secret) == null) throw new Exception();
		Contact contact = new Contact(
			new Liberaddress(map.get(liberaddress)),
			map.get(secret),
			Utils.decodeString(map.get(publicKey))
		);
		contact.update(new UserInfo(map.get(firstname), map.get(lastname), map.get(photo), map.get(status)));
		contact.setIgnored(Boolean.parseBoolean(map.get(ignored)));
		contact.setAccountFirstnameSent(Boolean.parseBoolean(map.get(accountFirstnameSent)));
		contact.setAccountLastnameSent(Boolean.parseBoolean(map.get(accountLastnameSent)));
		contact.setAccountPhotoSent(Boolean.parseBoolean(map.get(accountPhotoSent)));
		contact.setAccountStatusSent(Boolean.parseBoolean(map.get(accountStatusSent)));
		return contact;
	}
}
