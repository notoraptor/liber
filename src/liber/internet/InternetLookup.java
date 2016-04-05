package liber.internet;

public class InternetLookup extends Thread {
	private String distant;
	private boolean active;
	private InternetDependant dependant;
	public InternetLookup(String distantServer, InternetDependant internetDependant) {
		assert distantServer != null;
		distant = distantServer;
		active = true;
		dependant = internetDependant;
	}
	@Override
	public void run() {
		System.err.println("Internet lookup thread running.");
		if(!Internet.isConnected()) {
			while (active && !Internet.checkConnexion(distant)) {
				try {
					Thread.sleep(300);
				} catch(InterruptedException e) {
					System.err.println("Internet lookup thread interrupted.");
					dependant = null;
					break;
				}
			}
		}
		if(dependant != null) {
			new Thread(() -> {
				if(Internet.isConnected())
					dependant.connect();
				else
					dependant.disconnect();
			}).start();
		}
		System.err.println("Internet lookup thread terminated.");
	}
	public void cancel() {
		active = false;
	}
	public String address() {
		return distant;
	}
}
