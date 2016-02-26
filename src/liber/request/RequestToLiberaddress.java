package liber.request;

import liber.Libersaurus;
import liber.data.User;
import liber.enumeration.Field;
import liber.recipient.Location;
import liber.request.Request;

import java.util.Map;

/**
 * Created by HPPC on 21/02/2016.
 */
public abstract class RequestToLiberaddress extends Request {
	public RequestToLiberaddress(User recipient) {
		super(Libersaurus.current.account().liberaddress(), new Location(recipient));
		add(Field.secret, recipient.secret());
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
			s.append(entry.getKey()).append("\t").append(value).append("\r\n");
		}
		s.append("end\r\n");
		return s.toString();
	}
}
