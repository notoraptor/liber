package liber.notification;

/**
 * Created by HPPC on 23/02/2016.
 */
public class Info<T> {
	private T element;
	public Info(T theElement) {
		assert theElement != null;
		element = theElement;
	}
	public T get() {
		return element;
	}
}
