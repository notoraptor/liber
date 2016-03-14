package liber.request;

import liber.Libersaurus;
import liber.data.User;
import liber.enumeration.Field;
import liber.recipient.Liberserver;
import liber.recipient.Location;
import liber.request.Request;

import java.util.Map;

public abstract class RequestToLiberaddress extends Request {
	private User user;
	public RequestToLiberaddress(User recipient) {
		super(Libersaurus.current.account().liberaddress(), new Location(recipient));
		add(Field.secret, recipient.secret());
		user = recipient;
	}
	public Liberserver recipientLiberserver() {
		return user.liberserver();
	}
	public String recipientUsername() {
		return user.username();
	}
	@Override
	final public Field[] goodFields() {
		return null;
	}
	@Override
	final public Field[] badFields() {
		return null;
	}
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("request\t").append(name()).append("\r\n");
		s.append("sender\t").append(sender()).append("\r\n");
		s.append("recipient\t").append(recipientAddress()).append("\r\n");
		for (Map.Entry<Field, String> entry : parameters()) {
			String value = entry.getValue();
			if (value == null) value = "";
			s.append(entry.getKey()).append('\t').append(value).append("\r\n");
		}
		s.append("end\r\n");
		return s.toString();
	}
}
