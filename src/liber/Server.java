package liber;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

// TODO (?): LES CLASSES DE CE FICHIER SONT INCOMPLÈTES.
public class Server extends ServerInterface {
	static private String protocol = "TCP";
	private ServerSocket server; // Server
	private String privateIP; // discoverDeviceAndIPs
	private String publicIP; // discoverDeviceAndIPs
	private int privatePort; // Server
	private int publicPort; // makePublic
	private boolean runServer = true;
	private GatewayDevice device; // discoverDeviceAndIPs
	public Server() throws Exception {
		server = new ServerSocket(0);
		privatePort = server.getLocalPort();
		System.err.println("private port = " + privatePort);
		makePublic();
		System.out.println("PRIVATE: " + privateIP + ":" + privatePort);
		System.out.println("PUBLIC:  " + publicIP + ":" + publicPort);
	}
	public Server(int thePrivatePort, int thePublicPort) throws Exception {
		// server
		if(thePrivatePort <= 0) {
			server = new ServerSocket(0);
		} else try {
			server = new ServerSocket(thePrivatePort);
		} catch(Exception e) {
			server = new ServerSocket(0);
		}
		privatePort = server.getLocalPort();
		// makePublic
		if(thePublicPort < 0)
			makePublic();
		else
			makePublic(thePublicPort);
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
				if (!e.getMessage().toLowerCase().equals("socket closed")) {
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
		System.err.println("Server " + (isRunning() ? "encore ouvert." : "fermé."));
		try {
			makePrivate();
		} catch (Exception e) {
			// System.err.println("Erreur pendant la fermeture du compte: " + e.getMessage());
			// e.printStackTrace();
			throw new IOException(e);
		}
	}
	@Override
	public int publicPort() {
		return publicPort;
	}
	@Override
	public int privatePort() {
		return privatePort;
	}
	@Override
	public String publicIP() {
		return publicIP;
	}
	@Override
	public String privateIP() {
		return privateIP;
	}
	@Override
	synchronized public boolean isRunning() {
		return runServer && server.isClosed();
	}
	private void makePublic() throws Exception {
		discoverDeviceAndIPs();
		tryPortMapping();
		System.out.println("Mapping successful.");
	}
	private void makePublic(int thePublicPort) throws Exception {
		discoverDeviceAndIPs();
		PortMappingEntry portMapping = new PortMappingEntry();
		if(device.getSpecificPortMappingEntry(thePublicPort, protocol, portMapping)) {
			if(portMapping.getInternalPort() == this.privatePort) {
				this.publicPort = thePublicPort;
				System.out.println("Port mapping of previous session re-used.");
			} else {
				tryPortMapping(portMapping);
			}
		} else if(device.addPortMapping(thePublicPort, this.privatePort, this.privateIP, protocol, "libersaurus")) {
			this.publicPort = thePublicPort;
			System.out.println("Port mapping of previous session re-allocated.");
		} else {
			tryPortMapping(portMapping);
		}
		System.out.println("Mapping successful.");
	}
	private void makePrivate() throws Exception {
		device.deletePortMapping(this.publicPort, protocol);
	}
	private void discoverDeviceAndIPs() throws Exception {
		GatewayDiscover discover = new GatewayDiscover();
		Map<InetAddress, GatewayDevice> devices = discover.discover();
		System.err.println(devices.size() + " upnp devices.");
		device = discover.getValidGateway();
		if (device == null)
			throw new Exception("No valid gateway device found.");
		this.privateIP = device.getLocalAddress().getHostAddress();
		this.publicIP = device.getExternalIPAddress();
	}
	private void tryPortMapping(PortMappingEntry portMapping) throws Exception {
		int maxOffset = 100;
		int portOffset;
		for (portOffset = 0; portOffset <= maxOffset; ++portOffset) {
			this.publicPort = 10_000 + this.privatePort + portOffset;
			if (device.getSpecificPortMappingEntry(this.publicPort, protocol, portMapping))
				continue;
			if(device.addPortMapping(this.publicPort, this.privatePort, this.privateIP, protocol, "libersaurus"))
				break;
		}
		if (portOffset > maxOffset)
			throw new Exception("Port mapping attempt failed");
	}
	private void tryPortMapping() throws Exception {
		tryPortMapping(new PortMappingEntry());
	}
}
