package liber.data;

import liber.enumeration.Encoding;
import liber.exception.HashException;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Message {
	private Contact protagonist;
	private MessageContent content;
	public Message(Contact contact, String messageContent, Encoding encoding) {
		assert contact != null && messageContent != null;
		protagonist = contact;
		content = new MessageContent(messageContent, encoding);
	}
	public Contact protagonist() {
		return protagonist;
	}
	public Liberaddress liberaddress() {
		return protagonist.liberaddress();
	}
	public String encodedContent() {
		return content.toString();
	}
	public String decodedContent() {
		return content.decode();
	}
	public String getHash() throws HashException {
		return content.getHash();
	}
	public boolean hashIs(String messageHash) throws HashException {
		return content.getHash().equals(messageHash);
	}
	public String timeString() {
		Date date = new Date(microtime()/1000);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE dd LLL yyyy - HH ':' mm ':' ss");
		return sdf.format(date);
	}
	abstract public long microtime();
	abstract public MessageID id();
}
