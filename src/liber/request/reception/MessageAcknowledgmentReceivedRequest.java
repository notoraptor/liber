package liber.request.reception;

import liber.Libersaurus;
import liber.enumeration.Field;
import liber.exception.RequestException;
import liber.request.ReceivedRequest;

/**
 * Created by HPPC on 21/02/2016.
 */
public class MessageAcknowledgmentReceivedRequest extends ReceivedRequest {
	@Override
	public Field[] needed() {
		return new Field[]{Field.microtime};
	}
	@Override
	public void manage() throws RequestException {
		try {
			long microtime = Long.parseLong(get(Field.microtime));
			Libersaurus.current.aknowledgeMessage(sender(), secret(), microtime);
		} catch (NumberFormatException e) {
			throw RequestException.ERROR_MICROTIME_FORMAT;
		}
	}
}
