package liber.data;

import liber.enumeration.Field;
import liber.exception.AddressException;
import liber.request.Request;
import liber.request.Response;
import liber.request.server.GetServerPlaceRequest;

import java.net.InetAddress;

/**
 * Created by HPPC on 21/02/2016.
 */
public class Address {
	private InetAddress ip;
	private int port;
	public Address(Liberaddress liberaddress) throws AddressException {
		try {
			Response response = Request.sendRequest(new GetServerPlaceRequest(liberaddress));
			if (response == null || response.bad()) throw new AddressException(response.status());
			String ipString = response.get(Field.ip);
			String portString = response.get(Field.port);
			if (ipString == null) {
				ip = null;
				port = -1;
			} else {
				ip = InetAddress.getByName(ipString);
				port = Integer.parseInt(portString);
				if (port <= 0) throw new AddressException("port is " + port);
			}
		} catch (Exception e) {
			ip = null;
			port = -1;
			throw new AddressException(e);
		}
	}
	public boolean has() {
		return port > 0;
	}
	public InetAddress ip() {
		return ip;
	}
	public int port() {
		return port;
	}
}
