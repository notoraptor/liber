package liber.recipient;

import liber.data.User;
import liber.exception.OfflineLocationException;
import liber.request.Request;
import liber.request.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Location implements Recipient {
	private User user;
	public Location(User locationUser) {
		assert locationUser != null;
		user = locationUser;
	}
	@Override
	public boolean updatable() {
		return true;
	}
	@Override
	public void update() {
		try {
			user.updateAddress();
		} catch (Exception ignored) {}
	}
	@Override
	public String address() {
		return user.liberaddress().toString();
	}
	@Override
	public String serverAddress() {
		return user.liberaddress().liberserver().address();
	}
	@Override
	public Response receive(Request request) throws Exception {
		if (!user.hasAddress()) throw new OfflineLocationException();
		Response response;
		try (
				Socket echoSocket = new Socket(user.ip(), user.port());
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()))
		) {
			out.print(request.toString());
			out.flush();
			response = Response.parse(in);
		}
		return response;
	}
	@Override
	public String toString() {
		return user.liberaddress() + " (" + user.ip() + ":" + user.port() + ")";
	}
}
