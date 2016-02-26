package liber.card.textable;

import liber.card.Libercard;
import liber.data.Contact;
import liber.data.Liberaddress;
import liber.data.OutMessage;

import java.util.HashMap;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class TextableOutlink extends Textable<OutMessage> {
	static private String liberaddress = "liberaddress";
	static private String secret = "secret";
	static private String microtime = "microtime";
	static private String encodedContent = "encodedContent";
	public TextableOutlink(Libercard libercard, OutMessage message) {
		super(libercard, message);
	}
	@Override
	public String name() {
		return "outlink";
	}
	@Override
	public String[] fields() {
		return new String[]{liberaddress, secret, microtime, encodedContent};
	}
	@Override
	public void toText(HashMap<String, String> map) {
		map.put(liberaddress, get().liberaddress().toString());
		map.put(secret, get().recipient().secret());
		map.put(microtime, String.valueOf(get().microtime()));
		map.put(encodedContent, get().encodedContent());
	}
	@Override
	public OutMessage fromText(HashMap<String, String> map) throws Exception {
		Contact contact = new Contact(new Liberaddress(map.get(liberaddress)), map.get(secret));
		return new OutMessage(contact, Long.parseLong(map.get(microtime)), map.get(encodedContent));
	}
}
