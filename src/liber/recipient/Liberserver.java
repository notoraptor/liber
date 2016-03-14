package liber.recipient;

import liber.Utils;
import liber.exception.RecipientAddressException;
import liber.request.Request;
import liber.request.Response;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class Liberserver implements Recipient {
	static public final String end = "/server.php";
	static private final String publicKeyBasename = "/public.key";
	static private HashMap<String, StringBuilder> serverKeys = new HashMap<>();
	static public StringBuilder getServerKey(Liberserver liberserver) {
		String address = liberserver.serverAddress();
		address = address.substring(0, address.length() - end.length()) + publicKeyBasename;
		StringBuilder serverKey;
		if(serverKeys.containsKey(address)) {
			serverKey = serverKeys.get(address);
		} else {
			//serverKey = downloadServerKey(address);
			serverKey = download(address);
			serverKeys.put(address, serverKey);
		}
		return serverKey;
	}
	static public StringBuilder download(String address) {
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
		String requestString = request.toString();
		if(requestString.length() < 1024*32) {
			//System.err.println("{normal}");
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
			System.err.println("{fileUpload}");
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			File tempFile = new File(String.valueOf(System.currentTimeMillis()) + ".tmp");
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			writer.write(requestString);
			writer.close();
			builder.addPart("post", new FileBody(tempFile));
			HttpEntity entity = builder.build();
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(address);
			httpPost.setEntity(entity);
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			tempFile.delete();
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream stream = httpEntity.getContent();
			BufferedReader in = new BufferedReader(new InputStreamReader(stream));
			Response response = Response.parse(in);
			in.close();
			httpResponse.close();
			return response;
		}
	}
	@Override
	public String toString() {
		return address;
	}
}
