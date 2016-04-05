package liber.card.textable;

import liber.card.Libercard;
import liber.data.Contact;
import liber.data.InMessage;
import liber.data.Liberaddress;

import java.util.HashMap;

public class TextableInlink extends Textable<InMessage> {
	static private String liberaddress = "liberaddress";
	static private String secret = "secret";
	static private String microtime = "microtime";
	static private String encodedContent = "encodedContent";
	public TextableInlink(Libercard libercard, InMessage message) {
		super(libercard, message);
	}
	@Override
	public String name() {
		return "inlink";
	}
	@Override
	public String[] fields() {
		return new String[]{liberaddress, secret, microtime, encodedContent};
	}
	@Override
	public void toText(HashMap<String, String> map) {
		map.put(liberaddress, get().liberaddress().toString());
		map.put(secret, get().sender().secret());
		map.put(microtime, String.valueOf(get().microtime()));
		map.put(encodedContent, get().encodedContent());
	}
	@Override
	public InMessage fromText(HashMap<String, String> map) throws Exception {
		Contact contact = new Contact(new Liberaddress(map.get(liberaddress)), map.get(secret), null);
		return new InMessage(contact, Long.parseLong(map.get(microtime)), map.get(encodedContent));
	}
}
