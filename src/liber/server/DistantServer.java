package liber.server;

import liber.Libersaurus;

import java.util.Random;

public class DistantServer extends ServerInterface {
	static private final String publicIP = "liberserver";
	static private final String privateIP = "myLiberserver";
	static public boolean isDistant(String ip) {
		return (ip.equals(publicIP) || ip.equals(privateIP));
	}
	final private Libersaurus context;
	private boolean runServer;
	private Random random;
	public DistantServer(Libersaurus instance) {
		assert instance != null;
		context = instance;
		runServer = true;
		random = new Random();
	}
	@Override
	public void run() {
		while(runServer) {
			while (context.getNextPosted());
			long timeToWait = 800L + random.nextInt(101);
			try {
				Thread.sleep(timeToWait);
			} catch (InterruptedException e) {
				System.err.println("Interruption du serveur distant.");
				runServer = false;
			}
		}
		System.err.println("Arrêt du serveur distant.");
	}
	@Override
	public void close() {
		runServer = false;
	}
	@Override
	public int publicPort() {
		return 80;
	}
	@Override
	public int privatePort() {
		return 80;
	}
	@Override
	public String publicIP() {
		return DistantServer.publicIP;
	}
	@Override
	public String privateIP() {
		return DistantServer.privateIP;
	}
	@Override
	public boolean isRunning() {
		return runServer;
	}
}
