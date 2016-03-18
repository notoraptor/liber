package liber.request;

import liber.Libersaurus;
import liber.Utils;
import liber.data.Liberaddress;
import liber.enumeration.Field;
import liber.exception.RequestException;
import liber.request.reception.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.EnumMap;

public abstract class ReceivedRequest {
	private Liberaddress sender;
	private Liberaddress recipient;
	private ReceivedContent parameters;
	protected ReceivedRequest() {}
	static class InitialReceivedRequestLookup implements  ReceivedRequestLookup {
		@Override
		public ReceivedRequest lookup(ReceivedContent content) {
			init();
			receivedRequestLookup = new DefaultReceivedRequestLookup();
			return findConstructor(content);
		}
	}
	static class DefaultReceivedRequestLookup implements ReceivedRequestLookup {
		@Override
		public ReceivedRequest lookup(ReceivedContent content) {
			return findConstructor(content);
		}
	}
	static private EnumMap<RequestName, ReceivedRequestConstructor> parsers;
	static private ReceivedRequestLookup receivedRequestLookup = new InitialReceivedRequestLookup();
	static private ReceivedRequest findConstructor(ReceivedContent content) {
		try {
			RequestName requestName = RequestName.valueOf(content.get(Field.request));
			ReceivedRequestConstructor constructor = parsers.get(requestName);
			ReceivedRequest receivedRequest = constructor == null ? null : constructor.get();
			return receivedRequest;
		} catch (Exception e) {
			return null;
		}
	}
	static private void init() {
		System.err.println("[[[[]]]] Initialisation des analyseurs de requêtes reçues.");
		parsers = new EnumMap<>(RequestName.class);
		parsers.put(RequestName.contactDataDeleted,			() -> new ContactDataDeletedReceivedRequest());
		parsers.put(RequestName.contactDataUpdated,			() -> new ContactDataUpdatedReceivedRequest());
		parsers.put(RequestName.linkAccepted,				() -> new LinkAcceptedReceivedRequest());
		parsers.put(RequestName.linkCancelled,				() -> new LinkCancelledReceivedRequest());
		parsers.put(RequestName.linkDeleted,				() -> new LinkDeletedReceivedRequest());
		parsers.put(RequestName.linkOffer,					() -> new LinkOfferReceivedRequest());
		parsers.put(RequestName.linkRefused,				() -> new LinkRefusedReceivedRequest());
		parsers.put(RequestName.messageAcknowledgment,		() -> new MessageAcknowledgmentReceivedRequest());
		parsers.put(RequestName.message,					() -> new MessageReceivedRequest());
		parsers.put(RequestName.nowOffline,					() -> new NowOfflineReceivedRequest());
		parsers.put(RequestName.nowOnlineAcknowledgment,	() -> new NowOnlineAcknowledgmentReceivedRequest());
		parsers.put(RequestName.nowOnline,					() -> new NowOnlineReceivedRequest());

	}
	static public ReceivedRequest parse(BufferedReader in) throws RequestException {
		ReceivedContent content = new ReceivedContent();
		String line;
		try {
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) break;
				if (line.equals("end")) break;
				String[] pieces = line.split("\t", 2);
				if (pieces.length != 2)
					content.put(pieces[0].trim(), null);
				else
					content.put(pieces[0].trim(), pieces[1].trim());
			}
		} catch (IOException e) {
			throw RequestException.ERROR_READING_STREAM();
		}
		if (content.isEmpty())
			throw RequestException.REQUEST_ERROR_NO_REQUEST();
		ReceivedRequest receivedRequest = receivedRequestLookup.lookup(content);
		if(receivedRequest == null)
			throw RequestException.REQUEST_ERROR_UNKNOWN_REQUEST();
		receivedRequest.loadFrom(content);
		return receivedRequest;
	}
	protected void loadFrom(ReceivedContent content) throws RequestException {
		// Récupération des champs obligatoires.
		String request = content.request();
		String sender = content.sender();
		String recipient = content.recipient();
		String secret = content.secret();
		// Les champs ne doivent pas être nuls.
		if (request == null)
			throw RequestException.REQUEST_ERROR_NAME_MISSING();
		if (sender == null || sender.isEmpty())
			throw RequestException.REQUEST_ERROR_SENDER_MISSING();
		if (recipient == null || sender.isEmpty())
			throw RequestException.REQUEST_ERROR_RECIPIENT_MISSING();
		if (secret == null || secret.isEmpty())
			throw RequestException.REQUEST_ERROR_SECRET_MISSING();
		// La liberadresse de l'expéditeur doit être valide.
		try {
			this.sender = new Liberaddress(sender);
		} catch (Exception e) {
			throw RequestException.REQUEST_ERROR_SENDER();
		}
		// La liberadresse du destinataire doit être valide.
		try {
			this.recipient = new Liberaddress(recipient);
		} catch (Exception e) {
			throw RequestException.REQUEST_ERROR_RECIPIENT();
		}
		// Le destinataire de la requête doit être le compte courant.
		if (!this.recipient.equals(Libersaurus.current.account().liberaddress()))
			throw RequestException.REQUEST_ERROR_RECIPIENT();
		// L'expéditeur et le destinataire doivent être différents.
		if (this.sender.equals(this.recipient()))
			throw RequestException.REQUEST_ERROR_SENDER_IS_RECIPIENT();
		// Les champs obligatoires doivent être présents.
		Field[] needed = needed();
		if (needed != null) for (Field field : needed)
			if (!content.has(field))
				throw RequestException.REQUEST_ERROR_FIELD_MISSING(field);
		parameters = content;
	}
	final public String name() {
		return parameters.request();
	}
	final public String secret() {
		return parameters.secret();
	}
	final public Liberaddress sender() {
		return sender;
	}
	final public Liberaddress recipient() {
		return recipient;
	}
	final public String get(Field key) {
		return parameters.get(key);
	}
	final public boolean has(Field key) {
		return parameters.has(key);
	}
	public Field[] needed() {
		return null;
	}
	final public Response respond() throws RequestException {
		manage();
		return new Response();
	}
	abstract public void manage() throws RequestException;
}

