package liber.card.textable;

import liber.card.Libercard;
import liber.data.Contact;
import liber.data.Liberaddress;
import liber.data.OutMessage;
import liber.enumeration.MessageState;

import java.util.HashMap;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class TextableOutMessage extends Textable<OutMessage> {
	static private String liberaddress = "liberaddress";
	static private String microtime = "microtime";
	static private String encodedContent = "encodedContent";
	static private String state = "state";
	public TextableOutMessage(Libercard libercard, OutMessage message) {
		super(libercard, message);
	}
	@Override
	public String name() {
		return "outmessage";
	}
	@Override
	public String[] fields() {
		return new String[]{liberaddress, microtime, encodedContent, state};
	}
	@Override
	public void toText(HashMap<String, String> map) {
		map.put(liberaddress, get().liberaddress().toString());
		map.put(microtime, String.valueOf(get().microtime()));
		map.put(encodedContent, get().encodedContent());
		map.put(state, get().state().toString());
	}
	@Override
	public OutMessage fromText(HashMap<String, String> map) throws Exception {
		Contact contact = libercard().contacts.get(new Liberaddress(map.get(liberaddress)));
		OutMessage message = new OutMessage(contact, Long.parseLong(map.get(microtime)), map.get(encodedContent));
		contact.addMessage(message);
		MessageState messageState = MessageState.valueOf(map.get(state));
		switch (messageState) {
			case NOT_SENT:
				message.setNotSent();
				break;
			case LIBERSERVER_WAITING:
				message.setLiberserverWaiting();
				break;
			case CONFIRMATION_WAITING:
				message.setConfirmationWaiting();
				break;
			case SENT:
				message.setSent();
				break;
			default:
				break;
		}
		return message;
	}
}
