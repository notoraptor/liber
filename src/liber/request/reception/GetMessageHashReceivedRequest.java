package liber.request.reception;

import liber.Libersaurus;
import liber.enumeration.Field;
import liber.exception.RequestException;
import liber.request.ReceivedRequest;
import liber.request.Response;

public class GetMessageHashReceivedRequest extends ReceivedRequest {
	private String messageHash;
	@Override
	public Field[] needed() {
		return new Field[]{Field.microtime};
	}
	@Override
	public void manage() throws RequestException {
		try {
			long microtime = Long.parseLong(get(Field.microtime));
			messageHash = Libersaurus.current.getMessageHash(sender(), secret(), microtime);
		} catch (NumberFormatException e) {
			throw RequestException.ERROR_MICROTIME_FORMAT();
		}
	}
	@Override
	public Response respond() throws RequestException {
		manage();
		Response response = new Response();
		response.add(Field.messageHash, messageHash);
		return response;
	}
}
