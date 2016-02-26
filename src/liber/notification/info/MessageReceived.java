package liber.notification.info;

import liber.data.InMessage;
import liber.notification.Info;

/**
 * Created by HPPC on 24/02/2016.
 */
public class MessageReceived extends Info<InMessage> {
	public MessageReceived(InMessage message) {
		super(message);
	}
}
