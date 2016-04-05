package liber.internet;

import java.net.URL;
import java.net.URLConnection;

public class Internet {
	static private boolean connected = true;
	static public boolean isConnected() {
		return connected;
	}
	static public boolean checkConnexion(String serverTest) {
		try {
			URL url = new URL(serverTest);
			URLConnection connection = url.openConnection();
			connection.connect();
			connected = true;
		} catch (Exception e) {
			connected = false;
		}
		// System.err.println("Connected to Internet ? " + connected);
		return connected;
	}
	static public void main(String[] args) {
		checkConnexion("http://google.fr");
	}
}
