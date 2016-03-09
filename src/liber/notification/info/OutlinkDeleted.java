package liber.notification.info;

import liber.data.OutMessage;
import liber.notification.Info;

public class OutlinkDeleted extends Info<OutMessage> {
	public OutlinkDeleted(OutMessage message) {
		super(message);
	}
}
