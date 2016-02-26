package liber.notification.info;

import liber.data.InMessage;
import liber.notification.Info;

/**
 * Created by HPPC on 23/02/2016.
 */
public class InlinkDeleted extends Info<InMessage> {
	public InlinkDeleted(InMessage message) {
		super(message);
	}
}
