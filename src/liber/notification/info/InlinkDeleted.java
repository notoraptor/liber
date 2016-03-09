package liber.notification.info;

import liber.data.InMessage;
import liber.notification.Info;

public class InlinkDeleted extends Info<InMessage> {
	public InlinkDeleted(InMessage message) {
		super(message);
	}
}
