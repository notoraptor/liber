package liber.data;

import liber.server.DistantServer;
import liber.enumeration.Field;
import liber.exception.AddressException;
import liber.exception.RecipientException;
import liber.exception.RequestException;
import liber.request.Response;
import liber.request.requestSent.server.GetServerPlaceRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Address {
	private InetAddress ip;
	private int port;
	private boolean distant;
	public Address() {
		ip = null;
		port = -1;
		distant = false;
	}
	public Address(Liberaddress liberaddress) throws AddressException {
		try {
			Response response = new GetServerPlaceRequest(liberaddress).justSend();
			if (response.bad()) throw new AddressException(response);
			StringBuilder ipString = response.get(Field.ip);
			StringBuilder portString = response.get(Field.port);
			if (ipString == null) {
				ip = null;
				port = -1;
			} else if(DistantServer.isDistant(ipString.toString())) {
				distant = true;
				ip = null;
				port = -1;
			} else {
				ip = InetAddress.getByName(ipString.toString());
				port = Integer.parseInt(portString.toString());
				if (port <= 0) throw new AddressException(port);
			}
		} catch (RecipientException|RequestException|UnknownHostException e) {
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
	public boolean isDistant() {
		return distant;
	}
	public void clear() {
		ip = null;
		port = -1;
		distant = false;
	}
}
