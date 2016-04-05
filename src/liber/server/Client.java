package liber.server;

import liber.Libersaurus;
import liber.enumeration.Field;
import liber.exception.RequestException;
import liber.request.requestReceived.ReceivedRequest;
import liber.request.Response;

import java.io.*;
import java.net.Socket;

public class Client extends Thread {
	final private Socket client;
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
				int lineCount = 0;
				String readLine = null;
				String line;
				while((line = in.readLine()) != null) {
					readLine = line;
					++lineCount;
				}
				if(lineCount != 1)
					throw RequestException.REQUEST_ERROR_NO_REQUEST();
				String[] pieces = readLine.split("\t");
				if(pieces.length == 2)
					throw RequestException.REQUEST_ERROR_NO_REQUEST();
				if(!pieces[0].equals("requestBody"))
					throw RequestException.REQUEST_ERROR_FIELD_MISSING(Field.requestBody);
				StringBuilder requestContent = Libersaurus.current.account().decrypt(new StringBuilder(pieces[1]));
				try(BufferedReader reader = new BufferedReader(new StringReader(requestContent.toString()))) {
					ReceivedRequest receivedRequest = ReceivedRequest.parse(reader);
					Response response = receivedRequest.respond();
					out.print(response);
					out.flush();
				}
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
