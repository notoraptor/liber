package liber.notification.info;

import liber.data.OutMessage;
import liber.notification.Info;

public class OutMessageUpdated extends Info<OutMessage> {
	public OutMessageUpdated(OutMessage outMessage) {
		super(outMessage);
	}
}
