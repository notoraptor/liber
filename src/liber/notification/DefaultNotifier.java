package liber.notification;

public class DefaultNotifier implements Notifier {
	@Override
	public void good(String notification) {
		System.out.print("[");
		System.out.print(notification);
		System.out.println("]");
	}
	@Override
	public void bad(String notification) {
		System.out.print("(*");
		System.out.print(notification);
		System.out.println("*)");
	}
}
