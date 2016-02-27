package liber;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// TODO (?): LES CLASSES DE CE FICHIER SONT INCOMPLÈTES.
public class Server extends Thread implements Closeable {
	static private String protocol = "TCP";
	private ServerSocket server;
	private String privateIP;
	private String publicIP;
	private int privatePort;
	private int publicPort;
	private boolean runServer;
	private GatewayDevice d;
	public Server() throws Exception {
		server = new ServerSocket(0);
		privatePort = server.getLocalPort();
		runServer = true;
		makePublic();
		System.out.println("PRIVATE: " + privateIP + ":" + privatePort);
		System.out.println("PUBLIC:  " + publicIP + ":" + publicPort);
	}
	@Override
	public void run() {
		while (runServer) {
			try {
				Socket clientSocket = server.accept();
				Client client = new Client(clientSocket);
				client.start();
			} catch (Exception e) {
				if (!e.getMessage().equals("socket closed")) {
					System.err.println("Unable to accept client and/or init client handling.");
					e.printStackTrace(System.err);
				}
			}
		}
	}
	@Override
	synchronized public void close() throws IOException {
		runServer = false;
		server.close();
		System.err.println("Server " + (isRunning() ? "not " : "") + "closed.");
		try {
			makePrivate();
		} catch (Exception e) {
			// System.err.println("Erreur pendant la fermeture du compte: " + e.getMessage());
			// e.printStackTrace();
			throw new IOException(e);
		}
	}
	public int publicPort() {
		return publicPort;
	}
	public int privatePort() {
		return privatePort;
	}
	public String publicIP() {
		return publicIP;
	}
	public String privateIP() {
		return privateIP;
	}
	synchronized public boolean isRunning() {
		return runServer && server.isClosed();
	}
	private void makePublic() throws Exception {
		GatewayDiscover discover = new GatewayDiscover();
		discover.discover();
		d = discover.getValidGateway();
		if (d == null)
			throw new Exception("No valid gateway device found.");
		this.privateIP = d.getLocalAddress().getHostAddress();
		this.publicIP = d.getExternalIPAddress();
		PortMappingEntry portMapping = new PortMappingEntry();
		int maxOffset = 100;
		int portOffset;
		for (portOffset = 0; portOffset <= maxOffset; ++portOffset) {
			this.publicPort = 10_000 + this.privatePort + portOffset;
			if (!d.getSpecificPortMappingEntry(this.publicPort, "TCP", portMapping)
					&& d.addPortMapping(this.publicPort, this.privatePort, this.privateIP, protocol, "libersaurus")
					) {
				break;
			}
		}
		if (portOffset > maxOffset)
			throw new Exception("Port mapping attempt failed");
		System.out.println("Mapping successful.");
	}
	private void makePrivate() throws Exception {
		d.deletePortMapping(this.publicPort, protocol);
	}
}
