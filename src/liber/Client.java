package liber;

import liber.exception.RequestException;
import liber.request.ReceivedRequest;
import liber.request.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class Client extends Thread {
	private Socket client;
	public Client(Socket clientSocket) {
		client = clientSocket;
	}
	@Override
	public void run() {
		String s = client.getInetAddress().getHostAddress();
		int p = client.getPort();
		System.err.println("Connexion entrante (" + s + ": " + p + ").");
		try (
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))
		) {
			try {
				if (!Libersaurus.current.loaded()) {
					System.err.println("[] no account loaded.");
					out.flush();
					return;
				}
				ReceivedRequest receivedRequest = ReceivedRequest.parse(in);
				Response response = receivedRequest.respond();
				out.print(response);
				out.flush();
			} catch (RequestException e) {
				out.print(new Response(e.getMessage()));
				out.flush();
			}
		} catch (Exception e) {
			System.err.println("Unable to manage client.");
			e.printStackTrace(System.err);
		}
	}
}
