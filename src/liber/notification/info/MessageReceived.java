package liber.notification.info;

import liber.data.InMessage;
import liber.notification.Info;

/**
 liber
 ${PACKAGE_NAME} - 24/02/2016
 **/
public class MessageReceived extends Info<InMessage> {
	public MessageReceived(InMessage message) {
		super(message);
	}
}
