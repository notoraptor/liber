package liber.notification.info;

import liber.data.OutMessage;
import liber.notification.Info;

/**
 * Created by HPPC on 24/02/2016.
 */
public class MessageCreated extends Info<OutMessage> {
	public MessageCreated(OutMessage message) {
		super(message);
	}
}
