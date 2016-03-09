package liber.request.reception;

import liber.Libersaurus;
import liber.enumeration.Field;
import liber.exception.RequestException;
import liber.request.ReceivedRequest;

public class LinkOfferReceivedRequest extends ReceivedRequest {
	@Override
	public Field[] needed() {
		return new Field[]{Field.microtime, Field.invitation};
	}
	@Override
	public void manage() throws RequestException {
		try {
			long microtime = Long.parseLong(get(Field.microtime));
			Libersaurus.current.addInlink(sender(), secret(), microtime, get(Field.invitation));
		} catch (NumberFormatException e) {
			throw RequestException.ERROR_MICROTIME_FORMAT();
		}
	}
}
