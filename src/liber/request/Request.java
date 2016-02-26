package liber.request;

import liber.Internet;
import liber.Libersaurus;
import liber.Utils;
import liber.data.Liberaddress;
import liber.enumeration.Field;
import liber.exception.RecipientException;
import liber.exception.RequestException;
import liber.notification.Notification;
import liber.recipient.Recipient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract public class Request {
	private HashMap<Field, String> parameters;
	private String request;
	private String sender;
	private Recipient recipient;
	private Request(String theSender, Recipient theRecipient) {
		assert theSender != null && theRecipient != null;
		request = Utils.getRequestName(this);
		sender = theSender;
		recipient = theRecipient;
		parameters = new HashMap<>();
	}
	public Request(Liberaddress senderAddress, Recipient theRecipient) {
		this(senderAddress.toString(), theRecipient);
	}
	public Request(Recipient theRecipient) {
		this("candidate", theRecipient);
	}
	static public Response sendRequest(Request request, String message) {
		Response response = null;
		if(Internet.isConnected()) {
			try {
				response = request.send();
			} catch(RecipientException e) {
				if(Internet.checkConnexion(request.recipientAddress())) {
					if(message != null) Notification.bad(message);
				} else {
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
	final public String name() {
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
		System.err.println("Envoi d'une requête à " + recipient);
		Response response;
		try {
			response = recipient.receive(this);
		} catch (Exception e) {
			if (!recipient.updatable())
				throw new RecipientException(e);
			recipient.update();
			try {
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
}
