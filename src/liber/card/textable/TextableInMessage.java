package liber.card.textable;

import liber.card.Libercard;
import liber.data.Contact;
import liber.data.InMessage;
import liber.data.Liberaddress;

import java.util.HashMap;

public class TextableInMessage extends Textable<InMessage> {
	static private String liberaddress = "liberaddress";
	static private String microtime = "microtime";
	static private String encodedContent = "encodedContent";
	static private String acknowledgeLater = "acknowledgeLater";
	public TextableInMessage(Libercard libercard, InMessage message) {
		super(libercard, message);
	}
	@Override
	public String name() {
		return "inmessage";
	}
	@Override
	public String[] fields() {
		return new String[]{liberaddress, microtime, encodedContent};
	}
	@Override
	public void toText(HashMap<String, String> map) {
		map.put(liberaddress, get().liberaddress().toString());
		map.put(microtime, String.valueOf(get().microtime()));
		map.put(encodedContent, get().encodedContent());
		map.put(acknowledgeLater, String.valueOf(get().toBeAcknowledged()));
	}
	@Override
	public InMessage fromText(HashMap<String, String> map) throws Exception {
		Contact contact = libercard().contacts.get(new Liberaddress(map.get(liberaddress)));
		InMessage message = new InMessage(contact, Long.parseLong(map.get(microtime)), map.get(encodedContent));
		if(map.containsKey(acknowledgeLater)) {
			boolean al = Boolean.parseBoolean(map.get(acknowledgeLater));
			message.setAcknowledgeLater(al);
		}
		contact.addMessage(message, false);
		return message;
	}
}
