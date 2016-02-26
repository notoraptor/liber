package liber.request.server;

import liber.enumeration.Field;
import liber.request.RequestFromAccountToLiberserver;

/**
 * Created by HPPC on 21/02/2016.
 */
public class PostedMessageReceivedRequest extends RequestFromAccountToLiberserver {
	public PostedMessageReceivedRequest(String author, String microtime) {
		super();
		add(Field.author, author);
		add(Field.microtime, microtime);
	}
}
