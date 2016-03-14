package liber.recipient;

import liber.data.User;
import liber.exception.OfflineLocationException;
import liber.request.Request;
import liber.request.RequestToLiberaddress;
import liber.request.Response;
import liber.request.server.PostLaterRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Location implements Recipient {
	private User user;
	private boolean responseFromLocation;
	public Location(User locationUser) {
		assert locationUser != null;
		user = locationUser;
	}
	public boolean isResponseFromLocation() {
		return responseFromLocation;
	}
	@Override
	public boolean updatable() {
		return true;
	}
	@Override
	public void update() {
		user.updateAddress();
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
		responseFromLocation = true;
		// TODO: Pas sûr que ce soit ici qu'il faille gérer la gestion de l'envoi des requêtes vers les serveurs distants.
		if (user.addressIsDistant()) {
			responseFromLocation = false;
			return new PostLaterRequest((RequestToLiberaddress) request).justSend();
		}
		if (!user.hasAddress())
			throw new OfflineLocationException();
		try (
			Socket echoSocket = new Socket(user.ip(), user.port());
			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()))
		) {
			out.print(request.toString());
			out.flush();
			return Response.parse(in);
		}
	}
	@Override
	public String toString() {
		return user.liberaddress() + " (" + (user.addressIsDistant() ? "distant:" : "") + user.ip() + ':' + user.port() + ')';
	}
}
