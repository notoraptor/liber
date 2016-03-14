package liber.data;

import liber.Internet;
import liber.Libersaurus;
import liber.card.textable.TextableInMessage;
import liber.card.textable.TextableOutMessage;
import liber.exception.InternetException;
import liber.notification.Notification;
import liber.exception.AddressException;
import liber.notification.info.MessageCreated;
import liber.notification.info.MessageReceived;
import liber.request.Request;
import liber.request.Response;
import liber.request.client.MessageAcknowledgmentRequest;
import liber.request.client.MessageRequest;
import liber.request.server.CheckPostedMessageRequest;
import liber.request.server.PostMessageRequest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/*
* TODO IMPORTANT: À PROPOS DES IDENTIFIANTS DES MESSAGES D'UN CONTACT.
* Théoriquement, deux InMessage (ou OutMessage) pour un même contact ne peuvent pas avoir le même horodatage,
* donc, ils ne peuvent avoir le même MessageID.
*/
public class Contact extends User implements KnownUser {
	private boolean online;
	private UserInfo info;
	private TreeMap<MessageID, Message> history;
	private TreeSet<MessageID> acknowledgeLater;
	private TreeSet<MessageID> locationWaiting;
	private TreeSet<MessageID> liberserverWaiting;
	private TreeSet<MessageID> confirmationWaiting;
	private TreeSet<MessageID> notSent;
	private TreeSet<MessageID> sent;
	public Contact(Liberaddress liberaddress) throws AddressException {
		super(liberaddress);
		init();
	}
	public Contact(Liberaddress liberaddress, String theSecret) throws AddressException {
		super(liberaddress, theSecret);
		init();
	}
	private void init() {
		online = false;
		info = new UserInfo();
		acknowledgeLater = new TreeSet<>();
		history = new TreeMap<>();
		locationWaiting = new TreeSet<>();
		liberserverWaiting = new TreeSet<>();
		confirmationWaiting = new TreeSet<>();
		notSent = new TreeSet<>();
		sent = new TreeSet<>();
	}
	@Override
	public UserInfo info() {
		return info;
	}
	public boolean online() {
		return online;
	}
	public void setOnline() {
		online = true;
		updateAddress();
	}
	public void setOffline() {
		online = false;
		updateAddress();
	}
	public void addMessage(InMessage message, boolean inform) {
		history.put(message.id(), message);
		if(message.toBeAcknowledged())
			acknowledgeLater.add(message.id());
		if(inform) Notification.info(new MessageReceived(message));
	}
	public void addMessage(InMessage message) {
		addMessage(message, true);
	}
	public void addMessage(OutMessage message) {
		MessageID id = message.id();
		history.put(id, message);
		switch (message.state()) {
			case LOCATION_WAITING:
				locationWaiting.add(id);
				break;
			case LIBERSERVER_WAITING:
				liberserverWaiting.add(id);
				break;
			case CONFIRMATION_WAITING:
				confirmationWaiting.add(id);
				break;
			case NOT_SENT:
				notSent.add(id);
				break;
			case SENT:
				sent.add(id);
				break;
			default:break;
		}
		Notification.info(new MessageCreated(message));
	}
	public void updateOutMessage(OutMessage message) {
		if (message.recipient() == this) {
			MessageID id = message.id();
			if (history.containsKey(id)) {
				locationWaiting.remove(id);
				liberserverWaiting.remove(id);
				confirmationWaiting.remove(id);
				notSent.remove(id);
				sent.remove(id);
				switch (message.state()) {
					case LOCATION_WAITING:
						locationWaiting.add(id);
						break;
					case LIBERSERVER_WAITING:
						liberserverWaiting.add(id);
						break;
					case CONFIRMATION_WAITING:
						confirmationWaiting.add(id);
						break;
					case NOT_SENT:
						notSent.add(id);
						break;
					case SENT:
						sent.add(id);
						break;
					default:break;
				}
			}
		}
	}
	public OutMessage getOutMessage(long microtime) {
		return (OutMessage) history.get(MessageID.forOut(microtime));
	}
	public OutMessage getLastOutMessage() {
		OutMessage message = null;
		for (Message m : history.descendingMap().values()) {
			if (m instanceof OutMessage) {
				message = (OutMessage) m;
				break;
			}
		}
		return message;
	}
	public void clearHistory() {
		locationWaiting.clear();
		liberserverWaiting.clear();
		confirmationWaiting.clear();
		notSent.clear();
		sent.clear();
		history.clear();
	}
	public void checkLiberserverWaitingMessages() throws InternetException {
		TreeSet<MessageID> copy = new TreeSet<>(liberserverWaiting);
		for (MessageID id : copy) {
			OutMessage message = (OutMessage) history.get(id);
			Response response = Request.sendRequest(new CheckPostedMessageRequest(message));
			if(response == null) {
				if (Internet.isConnected()) break;
				else throw new InternetException();
			}
			if (response.status().equals("NO_MESSAGE")) {
				message.setConfirmationWaiting();
			}
		}
	}
	public void convertLocationWaitingToNotSent() {
		HashSet<MessageID> copy = new HashSet<>(locationWaiting);
		for(MessageID id: copy) {
			((OutMessage)history.get(id)).setNotSent();
		}
	}
	public void convertNotSentToLocationWaiting() {
		HashSet<MessageID> copy = new HashSet<>(notSent);
		for(MessageID id : copy) {
			((OutMessage)history.get(id)).setLocationWaiting();
		}
	}
	public Collection<Message> messages() {
		return history.values();
	}
	public boolean canSendMessage() {
		return locationWaiting.isEmpty() && liberserverWaiting.isEmpty() && notSent.isEmpty();
	}
	public void manageOnline() {
		sendAknowledgements();
		sendLocationWaitingMessages();
	}
	public void addToAcknowledgeLater(InMessage message) {
		message.setAcknowledgeLater(true);
		acknowledgeLater.add(message.id());
	}
	public int countLocationWaitingMessages() {
		return locationWaiting.size();
	}
	public void sendAknowledgements() {
		TreeSet<MessageID> copy = new TreeSet<>(acknowledgeLater);
		for(MessageID id : copy) {
			InMessage message = (InMessage) history.get(id);
			Response response = Request.sendRequest(new MessageAcknowledgmentRequest(message.sender(), message.microtime()));
			if(response == null) {
				System.err.println("null");
				break;
			} else {
				System.err.println("not null - " + response.status());
			}
			if(response.good()) {
				message.setAcknowledgeLater(false);
				acknowledgeLater.remove(id);
			}
		}
	}
	public void sendLocationWaitingMessages() {
		TreeSet<MessageID> copy = new TreeSet<>(locationWaiting);
		for(MessageID id : copy) {
			OutMessage message = (OutMessage) history.get(id);
			if(Request.sendRequest(new MessageRequest(message)) == null)
				break;
			message.setSent();
		}
		convertLocationWaitingToNotSent();
	}
	public void transferLocationWaitingMessagesToServers() {
		TreeSet<MessageID> copy = new TreeSet<>(locationWaiting);
		for(MessageID id : copy) {
			OutMessage message = (OutMessage) history.get(id);
			try {
				new PostMessageRequest(message).justSend();
				message.setLiberserverWaiting();
			} catch(Exception e) {
				Notification.bad("Tous les messages pour " + appellation() +
						" n'ont pas pu être envoyés dans sa boîte de réception.");
				break;
			}
		}
	}
	private String historyFilename() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return "Discussion-(" + username() + ")-(" + sdf.format(date) + ").history";
	}
	// TODO: Il faut sauver l'historique dans un format lisible pour un humain (préférer HTML).
	public void saveHistory() {
		try {
			File directory = new File(Libersaurus.current.getDirectory(), "histories");
			if (!directory.exists()) {
				if (!directory.mkdir())
					throw new Exception("Unable to create \"histories\" directory.");
			} else if (!directory.isDirectory())
				throw new Exception("Unable to use \"histories\" as directory (it's a file) to store histories.");
			File filename = new File(directory, historyFilename());
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename.getAbsolutePath()));
			for (Message message : history.values()) {
				StringBuilder s;
				if (message instanceof InMessage)
					s = new TextableInMessage(null, (InMessage) message).toText();
				else
					s = new TextableOutMessage(null, (OutMessage) message).toText();
				writer.write(s.toString());
				writer.newLine();
			}
			writer.close();
			Notification.good("L'historique de vos échanges avec " +
				appellation() + " a été sauvegardé dans le fichier: " + filename.getAbsolutePath() + '\n' +
				"Vous pouvez récupérer ou supprimer ce fichier selon vos besoins.");
		} catch (Exception e) {
			Notification.bad("Impossible de sauvegarder l'historique de vos échanges avec " + appellation() + '.');
			e.printStackTrace();
		}
	}
}
