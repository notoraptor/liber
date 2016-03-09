package liber.notification.info;

import liber.data.InMessage;
import liber.notification.Info;

public class MessageReceived extends Info<InMessage> {
	public MessageReceived(InMessage message) {
		super(message);
	}
}
