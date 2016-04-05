package liber.request.requestReceived.reception;

import liber.Libersaurus;
import liber.enumeration.Field;
import liber.request.requestReceived.ReceivedRequest;

public class NowOnlineAcknowledgmentReceivedRequest extends ReceivedRequest {
	@Override
	public Field[] needed() {
		return new Field[] {Field.waitingMessages};
	}
	@Override
	public void manage() {
		Libersaurus.current.setContactWatchMe(sender(), secret(), get(Field.waitingMessages));
	}
}
