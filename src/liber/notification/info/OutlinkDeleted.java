package liber.notification.info;

import liber.data.OutMessage;
import liber.notification.Info;

/**
 * Created by HPPC on 23/02/2016.
 */
public class OutlinkDeleted extends Info<OutMessage> {
	public OutlinkDeleted(OutMessage message) {
		super(message);
	}
}
