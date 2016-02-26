package liber.recipient;

import liber.Utils;
import liber.exception.RecipientAddressException;
import liber.request.Request;
import liber.request.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by HPPC on 21/02/2016.
 */
public class Liberserver implements Recipient {
	static public final String end = "/server.php";
	private String address;
	public Liberserver(String serverAddress) throws RecipientAddressException {
		address = serverAddress;
		if (!(Utils.urlIsValid(address) && address.endsWith(end))) {
			System.err.println(address);
			throw new RecipientAddressException();
		}
	}
	@Override
	public String address() {
		return address;
	}
	@Override
	public String serverAddress() {
		return address;
	}
	@Override
	public Response receive(Request request) throws Exception {
		//// Communication avec une URL.
		// Connexion.
		URL url = new URL(address);
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		// Envoi de données.
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		out.write(request.toString()); // URLEncoder.encode(value, "UTF-8")
		out.close();
		// Réception de la réponse.
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		Response response = Response.parse(in);
		// Fin de la communication.
		in.close();
		return response;
	}
}
