package liber.notification;

/**
 * Created by HPPC on 21/02/2016.
 */
public interface Notifier {
	void good(String notification);
	void bad(String notification);
	default void info(Info info) {
		// Nothing.
	}
}
