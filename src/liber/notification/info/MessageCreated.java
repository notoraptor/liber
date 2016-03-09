package liber.notification.info;

import liber.data.OutMessage;
import liber.notification.Info;

public class MessageCreated extends Info<OutMessage> {
	public MessageCreated(OutMessage message) {
		super(message);
	}
}
