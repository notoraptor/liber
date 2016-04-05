package liber.request.requestReceived.reception;

import liber.Libersaurus;
import liber.enumeration.Field;
import liber.exception.RequestException;
import liber.request.requestReceived.ReceivedRequest;

public class MessageReceivedRequest extends ReceivedRequest {
	@Override
	public Field[] needed() {
		return new Field[]{Field.microtime, Field.message};
	}
	@Override
	public void manage() throws RequestException {
		try {
			long microtime = Long.parseLong(get(Field.microtime));
			String message = get(Field.message);
			Libersaurus.current.addMessage(sender(), secret(), microtime, message);
		} catch (NumberFormatException e) {
			throw RequestException.ERROR_MICROTIME_FORMAT();
		}
	}
}
