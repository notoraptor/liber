package liber;

import java.io.Closeable;
import java.io.IOException;

public abstract class ServerInterface extends Thread implements Closeable {
	@Override
	abstract public void run();
	@Override
	abstract public void close() throws IOException;
	abstract public int publicPort();
	abstract public int privatePort();
	abstract public String publicIP();
	abstract public String privateIP();
	abstract public boolean isRunning();
}
