package liber.recipient;

import liber.Utils;
import liber.exception.RecipientAddressException;
import liber.request.requestSent.Request;
import liber.request.Response;
import liber.security.Secure;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.PublicKey;
import java.util.HashMap;

public class Liberserver implements Recipient {
	private String address;
	private PublicKey publicKey;
	public Liberserver(String serverAddress) throws RecipientAddressException {
		if (Utils.urlIsInvalid(serverAddress)) {
			throw new RecipientAddressException("Adresse de liber-serveur invalide: " + serverAddress);
		}
		if(!serverAddress.endsWith(end)) {
			if(serverAddress.endsWith("/"))
				serverAddress += endBase;
			else
				serverAddress += end;
		}
		address = serverAddress;
		publicKey = getServerPublicKey(serverAddress);
		if(publicKey == null)
			throw new RecipientAddressException("Impossible de récupérer une clé publique depuis le liberserveur "
					+ serverAddress);
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
		String requestString = "requestBody=" + Secure.encryptWithAESAndRSA(request, publicKey);
		if(requestString.length() < 1024*32) {
			//debug System.err.println("{normal}");
			//// Communication avec une URL.
			// Connexion.
			URL url = new URL(address);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			// Envoi de données.
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			out.write(requestString); // URLEncoder.encode(value, "UTF-8")
			out.close();
			// Réception de la réponse.
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			Response response = Response.parse(in);
			// Fin de la communication.
			in.close();
			return response;
		} else {
			//debug System.err.println("{fileUpload}");
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addPart("post", new FileStringBody(requestString));
			HttpEntity entity = builder.build();
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(address);
			httpPost.setEntity(entity);
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream stream = httpEntity.getContent();
			BufferedReader in = new BufferedReader(new InputStreamReader(stream));
			Response response = Response.parse(in);
			in.close();
			httpResponse.close();
			return response;
		}
	}
	static private final String endBase = "server.php";
	static private final String end = "/server.php";
	static private final String publicKeyBasename = "/public.key";
	static private final HashMap<String, PublicKey> serverKeys = new HashMap<>();
	static private StringBuilder download(String address) {
		//debug System.err.println("Downloading: " + address);
		StringBuilder downloaded = null;
		try {
			StringBuilder content = new StringBuilder();
			URL url = new URL(address);
			URLConnection connection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while((line = in.readLine()) != null) {
				content.append(line).append("\r\n");
			}
			in.close();
			downloaded = content;
		} catch (Throwable ignored) {}
		return downloaded;
	}
	static private PublicKey getServerPublicKey(String address) {
		String downloadAddress =  address.substring(0, address.length() - end.length()) + publicKeyBasename;
		PublicKey serverKey;
		if(serverKeys.containsKey(downloadAddress)) {
			serverKey = serverKeys.get(downloadAddress);
		} else {
			serverKey = Secure.getPublicKey(download(downloadAddress));
			serverKeys.put(downloadAddress, serverKey);
		}
		return serverKey;
	}
}
