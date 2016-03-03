package liber;

import liber.exception.RequestException;
import liber.request.ReceivedRequest;
import liber.request.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {
	private Socket client;
	public Client(Socket clientSocket) {
		client = clientSocket;
	}
	@Override
	public void run() {
		String clientIP = client.getInetAddress().getHostAddress();
		int clientPort = client.getPort();
		System.err.println("Connexion entrante (" + clientIP + ": " + clientPort + ").");
		try (
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))
		) {
			try {
				if (!Libersaurus.current.loaded()) {
					System.err.println("Aucun compte chargé.");
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
			System.err.println("Impossible de gérer un client.");
			e.printStackTrace();
		}
		if(!client.isClosed()) try {
			client.close();
			System.err.println("Client traité.");
		} catch (IOException ignored) {
			System.err.println("Impossible de fermet la connexion avec un client.");
		}
	}
}
