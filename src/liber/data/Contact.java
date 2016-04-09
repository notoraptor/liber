package liber.data;

import liber.Libersaurus;
import liber.card.Libercard;
import liber.card.textable.TextableContact;
import liber.card.textable.TextableInMessage;
import liber.card.textable.TextableOutMessage;
import liber.enumeration.ContactData;
import liber.notification.Notification;
import liber.notification.info.MessageCreated;
import liber.notification.info.MessageReceived;
import liber.request.requestSent.Request;
import liber.request.Response;
import liber.request.requestSent.client.MessageAcknowledgmentRequest;
import liber.request.requestSent.client.MessageRequest;
import liber.request.requestSent.server.PostMessageRequest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class Contact extends User implements KnownUser {
	private boolean accountPhotoSent;
	private boolean accountFirstnameSent;
	private boolean accountLastnameSent;
	private boolean accountStatusSent;
	private boolean online;
	private boolean ignored;
	private UserInfo info;
	private TreeMap<MessageID, Message> history;
	private TreeSet<MessageID> acknowledgeLater;
	private TreeSet<MessageID> locationWaiting;
	private TreeSet<MessageID> liberserverWaiting;
	private TreeSet<MessageID> confirmationWaiting;
	private TreeSet<MessageID> notSent;
	private TreeSet<MessageID> sent;
	public Contact(Liberaddress liberaddress) throws Exception {
		super(liberaddress);
		init();
	}
	public Contact(Liberaddress liberaddress, String theSecret, String pub) throws Exception {
		super(liberaddress, theSecret, pub);
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
	public boolean accountPhotoSent() {
		return accountPhotoSent;
	}
	public boolean accountFirstnameSent() {
		return accountFirstnameSent;
	}
	public boolean accountLastnameSent() {
		return accountLastnameSent;
	}
	public boolean accountStatusSent() {
		return accountStatusSent;
	}
	public void setAccountPhotoSent(boolean b) {
		accountPhotoSent = b;
	}
	public void setAccountFirstnameSent(boolean b) {
		accountFirstnameSent = b;
	}
	public void setAccountLastnameSent(boolean b) {
		accountLastnameSent = b;
	}
	public void setAccountStatusSent(boolean b) {
		accountStatusSent = b;
	}
	public boolean isIgnored() {
		return ignored;
	}
	public void setIgnored(boolean b) {
		ignored = b;
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
	private void registerMessage(InMessage message) {
		history.put(message.id(), message);
		if(message.toBeAcknowledged())
			acknowledgeLater.add(message.id());
	}
	public void addNotInformedMessage(InMessage message) {
		registerMessage(message);
	}
	public void addMessage(InMessage message) {
		registerMessage(message);
		Notification.info(new MessageReceived(message));
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
	public InMessage getInMessage(long microtime) {
		return (InMessage) history.get(MessageID.forIn(microtime));
	}
	public void clearHistory() {
		locationWaiting.clear();
		liberserverWaiting.clear();
		confirmationWaiting.clear();
		notSent.clear();
		sent.clear();
		history.clear();
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
	private File createHistoriesDirectory() throws Exception {
		// TODO: Il faut sauver l'historique dans un format lisible pour un humain (préférer HTML).
		File directory = new File(Libersaurus.current.getDirectory(), Libercard.historiesFoldername);
		if (!directory.exists()) {
			if (!directory.mkdir())
				throw new Exception("Impossible de créer le dossier \"" + directory.getAbsolutePath() + "\".");
		} else if (!directory.isDirectory())
			throw new Exception("Impossible de sauvegarder les historiques vers le chemin \""
					+ directory.getAbsolutePath() + "\" car c'est un fichier, pas un dossier.");
		return directory;
	}
	public String saveHistory() throws Exception {
		File directory = createHistoriesDirectory();
		File filename = new File(directory, historyFilename());
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename.getAbsolutePath()));
		// Contact
		StringBuilder s = new TextableContact(null, this).toText();
		writer.write(s.toString());
		writer.newLine();
		// Historique des messages.
		for (Message message : history.values()) {
			if (message instanceof InMessage)
				s = new TextableInMessage (null, (InMessage)  message).toText();
			else
				s = new TextableOutMessage(null, (OutMessage) message).toText();
			writer.write(s.toString());
			writer.newLine();
		}
		writer.close();
		return filename.getAbsolutePath();
	}
	public void setDataToSent(ContactData data) {
		switch (data) {
			case firstname:
				accountFirstnameSent = false;
				break;
			case lastname:
				accountLastnameSent = false;
				break;
			case photo:
				accountPhotoSent = false;
				break;
			case status:
			case contactStatus:
				accountStatusSent = false;
				break;
		}
	}
}
