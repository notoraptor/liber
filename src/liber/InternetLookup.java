package liber;

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
		if(!Internet.isConnected()) {
			while (active && !Internet.checkConnexion(distant)) {
				try {
					Thread.sleep(500);
				} catch(InterruptedException e) {
					break;
				}
			}
		}
		if(dependant != null) {
			new Thread(() -> dependant.setInternetState(Internet.isConnected())).start();
		}
	}
	public void cancel() {
		active = false;
	}
	public String address() {
		return distant;
	}
}
