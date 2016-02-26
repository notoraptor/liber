package liber.notification.info;

import liber.data.OutMessage;
import liber.notification.Info;

/**
 * Created by HPPC on 24/02/2016.
 */
public class OutMessageUpdated extends Info<OutMessage> {
	public OutMessageUpdated(OutMessage outMessage) {
		super(outMessage);
	}
}
