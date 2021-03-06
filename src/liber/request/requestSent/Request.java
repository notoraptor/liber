package liber.request.requestSent;

import liber.internet.Internet;
import liber.Libersaurus;
import liber.data.Liberaddress;
import liber.enumeration.Field;
import liber.exception.RecipientException;
import liber.exception.RequestException;
import liber.notification.Notification;
import liber.recipient.Recipient;
import liber.request.Response;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

abstract public class Request {
	static private long count = 0;
	private EnumMap<Field, String> parameters;
	private RequestName request;
	private String sender;
	private Recipient recipient;
	private Request(String theSender, Recipient theRecipient) {
		assert theSender != null && theRecipient != null;
		//request = Utils.getRequestName(this);
		request = getRequestName();
		sender = theSender;
		recipient = theRecipient;
		parameters = new EnumMap<>(Field.class);
	}
	public Request(Liberaddress senderAddress, Recipient theRecipient) {
		this(senderAddress.toString(), theRecipient);
	}
	public Request(Recipient theRecipient) {
		this("candidate", theRecipient);
	}
	static public Response sendRequest(Request request, String message) {
		Response response = null;
		//System.err.println("Internet? " + Internet.isConnected());
		if(Internet.isConnected()) {
			try {
				response = request.justSend();
			} catch(RecipientException e) {
				System.err.println("Check internet connexion.");
				if(Internet.checkConnexion(request.recipientAddress())) {
					System.err.println("Seems connected.");
					if(message != null) Notification.bad(message);
				} else {
					System.err.println("Not connected.");
					// TODO Il faut v�rifier que la recherche de la connexion Internet fonctionne bien
					// (ex. en d�connectant le PC pendant l'ex�cution du logiciel).
					Libersaurus.current.lookupInternet(request.recipientServerAddress());
					if(message != null) Notification.bad(message);
				}
				//e.printStackTrace();
			} catch(RequestException e) {
				if(message != null) Notification.bad(message);
				//e.printStackTrace();
			}
		} else if(message != null) Notification.bad(message);
		return response;
	}
	static public Response sendRequest(Request request) {
		return sendRequest(request, null);
	}
	public Response justSend() throws RecipientException, RequestException {
		return send();
	}
	final public RequestName name() {
		return request;
	}
	final public String sender() {
		return sender;
	}
	final public String recipientAddress() {
		return recipient.address();
	}
	final public String recipientServerAddress() {
		return  recipient.serverAddress();
	}
	private Response send() throws RecipientException, RequestException {
		Response response;
		try {
			System.err.println("Tentative " + (++count) + ": " + request + " -> " + recipient.address());
			response = recipient.receive(this);
		} catch (Exception e) {
			e.printStackTrace();
			if (!recipient.updatable())
				throw new RecipientException(e);
			try {
				System.err.println("Tentative+" + (++count) + ": " + request + " -> " + recipient);
				recipient.update();
				response = recipient.receive(this);
			} catch (Exception f) {
				throw new RecipientException(f);
			}
		}
		manageResponse(response);
		return response;
	}
	final public void add(Field key, Object value) {
		parameters.put(key, value == null ? null : value.toString());
	}
	final public Set<Map.Entry<Field, String>> parameters() {
		return parameters.entrySet();
	}
	// Normalement abstract.
	public Field[] goodFields() {
		return null;
	}
	public Field[] badFields() {
		return null;
	}
	public void manageResponse(Response response) throws RequestException {
		/*
		System.out.println("<RESPONSE>");
		System.out.print(response);
		System.out.println("</RESPONSE>");
		*/
		if (response.good()) {
			Field[] fields = goodFields();
			if (fields != null) for (Field field : fields)
				if (!response.has(field))
					throw RequestException.RESPONSE_ERROR_GOOD_FIELD_MISSING(field);
		} else {
			Field[] fields = badFields();
			if (fields != null) for (Field field : fields)
				if (!response.has(field))
					throw RequestException.RESPONSE_ERROR_BAD_FIELD_MISSING(field);
			//throw liber.RequestException.RESPONSE_STATUS(response.status());
		}
	}
	abstract protected RequestName getRequestName();
}
