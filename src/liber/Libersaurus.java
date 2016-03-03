package liber;

import liber.card.Libercard;
import liber.data.*;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.exception.*;
import liber.notification.Notification;
import liber.notification.info.*;
import liber.request.Request;
import liber.request.Response;
import liber.request.client.*;
import liber.request.server.GetNextPostedMessageRequest;
import liber.request.server.LogoutRequest;
import liber.request.server.PostMessageRequest;
import liber.request.server.PostedMessageReceivedRequest;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

/* TODO GÉNÉRAL
TODO Contacts ignorés
	(dans un 4ème onglet de la page de travail).
	(pas de notifications sonores pour ces contacts).
TODO Distinction messages lus / non lus (complexe à mettre en place).
	Créer une nouvelle classe dans l'historique des contacts: messages non lus (HasMap<> unread ...).
	Message reçu lu ssi:
		Affiché au moins une fois à l'écran sur la page de discussion (visible dans le scrollpane).
TODO Gestion de l'affichage de la discussion.
	À la place du système actuel, il vaut peut-être mieux mettre à jour totalement la discussion à chaque nouveau message.
	Se rappeler que les observable lists sont peut-être utilisables pour bien le faire.
TODO Paramètres du programme.
	Activer/Désactiver toutes les notifications sonores.
	Activer/Désactiver l'utilisation des boîtes aux lettres (ne pas envoyer de messages en attente sur les serveurs).
*/

public class Libersaurus implements Closeable, InternetDependant {
	static public Libersaurus current;
	private File directory;
	private Configuration configuration;
	private Server server;
	private Features features;
	private Libercard libercard;
	private String password;
	private InternetLookup internetLookup;
	public Libersaurus() throws Exception {
		directory = Utils.workingDirectory();
		configuration = new Configuration(directory);
		server = new Server(configuration.getPrivatePort(), configuration.getPublicPort());
		server.start();
		configuration.setPorts(server.privatePort(), server.publicPort());
		libercard = null;
		password = null;
		internetLookup = null;
		features = new Features(this);
		current = this;
	}
	@Override
	public void close() {
		if(online())
			features().logout();
		if(loaded()) try {
			libercard.save();
			libercard = null;
			password = null;
		} catch (Exception e) {
			//throw new IOException(e);
			System.err.println("Impossible de sauvegarder correctement la liber-carte à la sortie du programme.");
		}
		if(internetLookup != null) {
			internetLookup.cancel();
			internetLookup.interrupt();
		}
		try {
			server.close();
		} catch (IOException e) {
			System.err.println("Impossible de fermer correctement le serveur local à la sortie du programme.");
			System.err.println("Il se peut que le port d'écoute (privé: " + server.privatePort() +
					", public: " + server.publicPort() + ") soit toujours ouvert.");
			System.err.println("Veuillez vérifier les serveurs virtuels (NAT/virtual servers) de votre routeur.");
		}
		try {
			configuration.save();
		} catch (Exception e) {
			System.err.println("Impossible de sauvegarder la configuration de Libersaurus (" + e.getMessage() + ").");
		}
	}
	public void lookupInternet(String address) {
		assert internetLookup == null;
		internetLookup = new InternetLookup(address, this);
		internetLookup.start();
	}
	@Override
	public void setInternetState(boolean connected) {
		if(internetLookup != null) {
			String previousAddress = internetLookup.address();
			if(internetLookup.isAlive()) try {
				internetLookup.join();
			} catch(InterruptedException ignored) {}
			internetLookup = null;
			if(connected)
				if(loaded()) reLogin();
			else
				lookupInternet(previousAddress);
		}
	}
	public File getDirectory() {
		return directory;
	}
	private void reLogin() {
		finalizeLogin();
	}
	private void load(Liberaddress liberaddress, String accountPassword) throws LibercardException {
		try {
			libercard = Libercard.load(liberaddress);
			if (libercard == null) {
				create(liberaddress, accountPassword);
			} else {
				password = accountPassword;
				System.err.println("[Libercarte chargée.]");
			}
		} catch (Exception e) {
			throw new LibercardException(e);
		}
	}
	private void finalizeLogin() {
		String fatalMessage = "Impossible de communiquer avec votre liber-serveur.\n" +
				"Êtes-vous connecté à Internet?\n" +
				"La procédure de connexion reprendra automatiquement lorsqu'une connexion à Internet sera détectée.";
		// Vérifier si les messages envoyés sur les serveurs des contacts ont été reçus par ces derniers.
		for (Contact contact : contacts()) try {
			contact.checkLiberserverWaitingMessages();
		} catch(InternetException e) {
			Notification.bad("Impossible de vérifier si vos précédents messages sont arrivés chez vos contacts.\n" +
					"Vérifiez votre connexion Internet.\n" +
					"La procédure de connexion reprendra lorsqu'une connexion à Internet sera détectée.");
			return;
		}
		// Récupérer les messages en attente pour ce compte sur le liber-serveur.
		HashSet<Contact> senders = new HashSet<>();
		do {
			Response response = Request.sendRequest(new GetNextPostedMessageRequest(), fatalMessage);
			if (response == null) return;
			if (response.bad()) {
				if (!response.status().equals("NO_MESSAGE"))
					Notification.bad("Reseponse error, unexpected response.");
				break;
			}
			String sender = response.get(Field.sender);
			String microtimeString = response.get(Field.microtime);
			String content = response.get(Field.message);
			if (Request.sendRequest(new PostedMessageReceivedRequest(sender, microtimeString), fatalMessage) == null)
				return;
			try {
				long microtime = Long.parseLong(microtimeString);
				Contact contact = getContact(sender);
				if (contact != null) {
					InMessage message = new InMessage(contact, microtime, content);
					contact.addMessage(message);
					Response mar = Request.sendRequest(new MessageAcknowledgmentRequest(contact, microtime));
					if(mar == null || mar.bad()) {
						contact.addToAcknowledgeLater(message);
					}
					senders.add(contact);
				}
			} catch (Exception ignored) {}
		} while (true);
		if(!senders.isEmpty()) {
			int sendersCount = senders.size();
			StringBuilder s = new StringBuilder();
			for(Contact contact: senders) {
				if(s.length() > 0)
					s.append(sendersCount == 1 ? " et " : ", ");
				s.append(contact.appellation());
				--sendersCount;
			}
			Notification.good("Vous avez reçu des messages de " +  s.toString() + ".");
		}
		// Déterminer pour chaque contact si la discussion est ouverte ou fermée.
		for(Contact contact: contacts()) {
			if(senders.contains(contact)) {
				// Discussion fermée (bloquée).
				// Le contact a envoyé des messages. L'utilisateur courant doit en tenir compte.
				// Donc les messages éventuellement en attente locale sont désormais invalides.
				contact.convertLocationWaitingToNotSent();
			} else {
				// Discussion ouverte.
				// Le contact n'a pas envoyé de messages, l'utilisateur courant peut lui écrire librement.
				// Donc les messages éventuellement non envoyés redeviennent valides.
				contact.convertNotSentToLocationWaiting();
			}
		}
		// Notifier tous les contacts de la connexion.
		// On peut en même temps vérifier quels contacts sont en ligne.
		for (Contact contact : contacts()) {
			try {
				Response response = Request.sendRequest(new NowOnlineRequest(contact));
				if(response != null && response.good()) {
					contact.setOnline();
					boolean updated = false;
					Field[] toCheck = new Field[]{
							Field.firstname,
							Field.lastname,
							Field.photo,
							Field.contactStatus,
					};
					for(Field field: toCheck) if(response.has(field)) {
						updated = true;
						contact.update(ContactData.valueOf(field.toString()), response.get(field));
					}
					if(updated) Notification.info(new ContactUpdated(contact));
					int m = Integer.valueOf(response.get(Field.waitingMessages));
					contact.sendAknowledgements();
					if(m == 0) {
						// Le contact n'a aucun message à envoyer.
						// Envoyer les message en attente locale.
						contact.sendLocationWaitingMessages();
					} else if(m > 0) {
						// Le contact va envoyer des messages.
						// Les messages éventuellement en attente locale sont invalides.
						contact.convertLocationWaitingToNotSent();
					}
				}
			} catch (Exception ignored) {} // Le contact est peut-être déconnecté.
		}
		//Notification.good("Connexion réussie.");
		// TODO: Pour débuggage: afficher les informations de la libercarte.
		printLibercard();
	}
	private void finalizeLogout() throws Exception {
		if (libercard != null) {
			libercard.save();
			libercard = null;
			password = null;
		}
	}
	private void printLibercard() {
		libercard.print();
	}
	// Accesseurs.
	public boolean loaded() {
		return libercard != null;
	}
	public boolean online() {
		return libercard != null && libercard.account.confirmed();
	}
	public String publicIP() {
		return server.publicIP();
	}
	public String privateIP() {
		return server.privateIP();
	}
	public int publicPort() {
		return server.publicPort();
	}
	public int privatePort() {
		return server.privatePort();
	}
	public Account account() {
		return libercard.account;
	}
	public String password() {
		return password;
	}
	public Features features() {
		return features;
	}
	public Contact getContact(String sender) {
		try {
			Liberaddress liberaddress = new Liberaddress(sender);
			return libercard.contacts.get(liberaddress);
		} catch (Exception e) {
			return null;
		}
	}
	public Contact getContact(Liberaddress sender) {
		return libercard.contacts.get(sender);
	}
	public Collection<Contact> contacts() {
		return libercard.contacts.list();
	}
	public Collection<InMessage> inlinks() {
		return libercard.inlinks.invitations();
	}
	public Collection<OutMessage> outlinks() {
		return libercard.outlinks.invitations();
	}
	// Gestion du compte.
	public void setContactOffline(Liberaddress sender, String secret) {
		Contact contact = libercard.contacts.get(sender);
		if (contact != null && contact.secretIs(secret)) {
			contact.setOffline();
			//Notification.good("liber.Contact " + contact.appellation() + " is disconnected.");
			Notification.info(new ContactUpdated(contact));
		}
	}
	public void setContactOnline(Liberaddress sender, String secret, UserInfo info) throws RequestException {
		Contact contact = libercard.contacts.get(sender);
		if (contact != null && contact.secretIs(secret)) {
			contact.manageOnline();
			contact.update(info);
			//Notification.good("liber.Contact " + contact.appellation() + " is connected.");
			Notification.info(new ContactUpdated(contact));
		} else throw RequestException.ERROR_CONTACT;
	}
	public void updateContactData(Liberaddress sender, String secret, ContactData dataType, String dataValue) {
		Contact contact = libercard.contacts.get(sender);
		if (contact != null && contact.secretIs(secret)) {
			contact.update(dataType, dataValue);
			Notification.info(new ContactUpdated(contact, dataType));
		}
	}
	public void deleteContactData(Liberaddress sender, String secret, ContactData dataType) {
		Contact contact = libercard.contacts.get(sender);
		if (contact != null && contact.secretIs(secret)) {
			contact.delete(dataType);
		}
	}
	public void deleteInlink(Liberaddress sender, String secret) {
		InMessage invitation = libercard.inlinks.get(sender);
		if (invitation != null) {
			Contact contact = invitation.sender();
			if (contact.secretIs(secret)) {
				libercard.inlinks.remove(invitation);
				Notification.good("L'utilisateur " + contact.appellation() +
						" a annulé la demande de mise en relation qu'il vous avait envoyée.");
				Notification.info(new InlinkDeleted(invitation));
			}
		}
	}
	public void deleteOutlink(Liberaddress sender, String secret) {
		OutMessage invitation = libercard.outlinks.get(sender);
		if (invitation != null) {
			Contact contact = invitation.recipient();
			if (contact.secretIs(secret)) {
				libercard.outlinks.remove(invitation);
				Notification.good("L'utilisateur " + contact.appellation() + " a refusé votre demande de contact.");
				Notification.info(new OutlinkDeleted(invitation));
			}
		}
	}
	public void validateContact(Liberaddress sender, String secret) throws RequestException {
		OutMessage invitation = libercard.outlinks.get(sender);
		if (invitation == null) throw RequestException.ERROR_OUTLINK_UNKNOWN;
		Contact contact = invitation.recipient();
		if (!contact.secretIs(secret)) throw RequestException.ERROR_SECRET;
		libercard.outlinks.remove(invitation);
		libercard.contacts.add(contact);
		contact.addMessage(invitation);
		invitation.setSent();
		Notification.good(contact.appellation() + " a accepté votre demande de mise en relation!");
		Notification.info(new OutlinkAccepted(invitation));
	}
	public void addInlink(Liberaddress sender, String secret, long microtime, String invitation) throws RequestException {
		if (libercard.contacts.has(sender)) throw RequestException.ERROR_INLINK;
		if (libercard.inlinks.has(sender)) throw RequestException.ERROR_INLINK;
		if (libercard.outlinks.has(sender)) throw RequestException.ERROR_INLINK;
		try {
			Contact contact = new Contact(sender, secret);
			InMessage inlink = new InMessage(contact, microtime, invitation);
			libercard.inlinks.add(inlink);
			Notification.good("Vous avez reçu une demande de contact de la part de " + contact.appellation() + ".");
			Notification.info(new LinkOfferReceived(inlink));
		} catch (AddressException e) {
			throw RequestException.ERROR_USER_ADDRESS;
		}
	}
	public void addMessage(Liberaddress sender, String secret, long microtime, String content) throws RequestException {
		Contact contact = libercard.contacts.get(sender);
		if (contact == null) throw RequestException.ERROR_CONTACT;
		if (!contact.secretIs(secret)) throw RequestException.ERROR_SECRET;
		InMessage message = new InMessage(contact, microtime, content);
		contact.addMessage(message);
	}
	public String getMessageHash(Liberaddress sender, String secret, long microtime) throws RequestException {
		Contact contact = libercard.contacts.get(sender);
		if (contact == null) throw RequestException.ERROR_CONTACT;
		if (!contact.secretIs(secret)) throw RequestException.ERROR_SECRET;
		OutMessage message = contact.getOutMessage(microtime);
		if (message == null) throw RequestException.ERROR_NO_MESSAGE;
		try {
			return message.getHash();
		} catch (HashException e) {
			throw RequestException.ERROR_HASH;
		}
	}
	public void aknowledgeMessage(Liberaddress sender, String secret, long microtime) throws RequestException {
		Contact contact = libercard.contacts.get(sender);
		if (contact == null) throw RequestException.ERROR_CONTACT;
		if (!contact.secretIs(secret)) throw RequestException.ERROR_SECRET;
		OutMessage message = contact.getOutMessage(microtime);
		if (message == null || (!message.isConfirmationWaiting() && !message.isLiberserverWaiting()))
			throw RequestException.ERROR_NO_MESSAGE;
		message.setSent();
		Notification.info(new OutMessageUpdated(message));
	}
	public void checkContactDeletion(Liberaddress sender, String secret) throws RequestException {
		Contact contact = libercard.contacts.get(sender);
		if (contact == null) throw RequestException.ERROR_CONTACT;
		if (!contact.secretIs(secret)) throw RequestException.ERROR_SECRET;
		libercard.deleteContact(contact);
		Notification.contactDeleted(contact);
		Notification.info(new ContactDeleted(contact));
	}
	// Action.
	public void create(Liberaddress liberaddress, String accountPassword) {
		password = accountPassword;
		libercard = new Libercard(liberaddress);
		Notification.good("Libercarte créée.");
	}
	public void login(Liberaddress liberaddress, String password) throws LibercardException {
		load(liberaddress, password);
		libercard.account.confirm();
		finalizeLogin();
	}
	public void loginToDelete(Liberaddress liberaddress, String password) throws LibercardException {
		load(liberaddress, password);
		libercard.account.setToDelete();
	}
	public void loginToConfirm(Liberaddress liberaddress, String password) throws LibercardException {
		load(liberaddress, password);
		libercard.account.setToConfirm();
	}
	public void logout() {
		// TODO: à retravailler (ordre des différentes opérations, opérations supplémentaires, etc.).
		// Notifier tous les contacts de la déconnexion (ignorer les erreurs ici).
		for (Contact contact : contacts()) {
			try {
				new NowOfflineRequest(contact).justSend();
			} catch (Exception ignored) {}
		}
		// Transférer les messages en attente vers leurs liber-serveurs respectifs.
		for (Contact contact : contacts())
			contact.transferLocationWaitingMessagesToServers();
		// Se déconnecter effectivement.
		try {
			Response response = new LogoutRequest().justSend();
			if (response.good()) {
				try {
					finalizeLogout();
				} catch (Exception e) {
					Notification.bad("Une erreur est survenur pendant la fermeture locale du compte.");
				}
			} else {
				Notification.bad("Impossible de se déconnecter correctement (" + response.status() + ").");
			}
		} catch (Exception e) {
			Notification.bad("Impossible de notifier la déconnexion à votre liber-serveur.");
		}
	}
	public void delete() throws LibercardException {
		if (libercard != null) {
			for(Contact contact: contacts()) {
				try {
					new LinkDeletedRequest(contact).justSend();
				} catch(Exception ignored) {}
			}
			libercard.delete();
			libercard = null;
			password = null;
		}
	}
	public void cancelOutlink(Liberaddress liberaddress) {
		OutMessage message = libercard.outlinks.get(liberaddress);
		if (message == null) {
			Notification.bad("You have not sent link request to that liberaddress.");
		} else {
			Response response = Request.sendRequest(new LinkCancelledRequest(message.recipient()));
			if (response != null) {
				if (response.bad())
					Notification.bad("Unable to inform user that you have canceled your link request. Link still available.");
				else {
					libercard.outlinks.remove(message);
					Notification.good("Demande annulée.");
				}
			}
		}
	}
	public void deleteContact(Liberaddress liberaddress) {
		Contact contact = libercard.contacts.get(liberaddress);
		if (contact == null)
			Notification.bad("Cette liber-adresse n'est pas parmi vos contacts.");
		else {
			Response response = Request.sendRequest(new LinkDeletedRequest(contact));
			if (response == null || response.bad()) {
				Notification.bad("Impossible d'informer le contact que vous avez supprimé la relation.\n" +
						"Le contact est toujours parmi vos relations.");
			} else {
				libercard.deleteContact(contact);
				/*
				Note: il faut aussi s'occuper des waitingMessages
				et des messages en attente pour ce contact envoyés sur son liberserveur.
				*/
				Notification.good("Contact supprimé.");
			}
		}
	}
	public void clearHistory(Liberaddress liberaddress) {
		Contact contact = libercard.contacts.get(liberaddress);
		if (contact == null) Notification.bad("You have not that contact.");
		else {
			contact.clearHistory();
			Notification.good("History cleared.");
		}
	}
	public void acceptInlink(Liberaddress liberaddress) {
		InMessage message = libercard.inlinks.get(liberaddress);
		if (message == null) Notification.bad("You have not received link from that liberaddress");
		else {
			Response response = Request.sendRequest(new LinkAcceptedRequest(message.sender()));
			if (response != null) if (response.bad()) {
				Notification.bad("Unable to inform user that you have accepted the link. Link still to be accepted.");
			} else {
				Contact contact = message.sender();
				libercard.inlinks.remove(message);
				libercard.contacts.add(contact);
				contact.addMessage(message, false);
				response = Request.sendRequest(new NowOnlineRequest(contact));
				if(response != null && response.good()) {
					contact.setOnline();
					boolean updated = false;
					Field[] toCheck = new Field[]{Field.firstname, Field.lastname, Field.photo, Field.contactStatus};
					for(Field field: toCheck) if(response.has(field)) {
						updated = true;
						contact.update(ContactData.valueOf(field.toString()), response.get(field));
					}
					if(updated) Notification.info(new ContactUpdated(contact));
				}
				Notification.good("Contact créé.");
			}
		}
	}
	public void refuseInlink(Liberaddress liberaddress) {
		InMessage message = libercard.inlinks.get(liberaddress);
		if (message == null) Notification.bad("You have not received link from that liberaddress");
		else {
			Response response = Request.sendRequest(new LinkRefusedRequest(message.sender()));
			if (response != null) if (response.bad()) {
				Notification.bad("Unable to inform user that you have refused the link. Link still to be refused.");
			} else {
				libercard.inlinks.remove(message);
				Notification.good("Link refused.");
			}
		}
	}
	public void newMessage(Liberaddress liberaddress, String theMessage) {
		Contact contact = libercard.contacts.get(liberaddress);
		if (contact == null) {
			Notification.bad("Cette liber-adresse n'est pas parmi vos contacts.");
			return;
		}
		boolean canSendMessage = contact.canSendMessage();
		OutMessage message = new OutMessage(contact, theMessage);
		contact.addMessage(message);
		if (canSendMessage) {
			Response response = Request.sendRequest(new MessageRequest(message));
			if(response == null)
				System.err.println("Le message n'a pas pu être envoyé maintenant. Il sera envoyé plus tard.");
			else if(response.good()) {
				message.setSent();
			} else {
				System.err.println("Impossible d'envoyer directement le message (" +
					response.status() + "). Le message sera envoyé plus tard.");
			}
		} else {
			System.err.println("Contact has not yet received last sent messages. This message will be sent later.");
		}
	}
	public void updateInfo(ContactData data, String value) {
		libercard.account.update(data, value);
	}
	public void deleteInfo(ContactData data) {
		libercard.account.delete(data);
	}
	public void createOutlink(Liberaddress recipient, String message) {
		if (libercard.contacts.has(recipient))
			Notification.bad("You have already that contact");
		if (libercard.inlinks.has(recipient))
			Notification.bad("You have already received a link offer of that liberaddress");
		if (libercard.outlinks.has(recipient))
			Notification.bad("You have already sent a link offer to that liberaddress.");
		else try {
			Contact potentialContact = new Contact(recipient);
			OutMessage outlink = new OutMessage(potentialContact, message);
			Response response = Request.sendRequest(
					new LinkOfferRequest(outlink), "Impossible d'envoyer votre demande de mise en relation. Raisons possibles:\n" +
							"1) L'utilisateur n'existe pas.\n" +
							"2) L'utilisateur est actuellement déconnecté.\n" +
							"3) Vous n'êtes pas connecté à Internet.");
			if (response != null) if (response.good()) {
				libercard.outlinks.add(outlink);
				Notification.good("Votre demande a été envoyé! L'utilisateur vous répondra bientôt.");
			} else {
				Notification.bad("Impossible d'envoyer la demande de mise en relation (" + response.status() + ").");
			}
		} catch (AddressException e) {
			Notification.bad("Unable to retrieve user lcoation on the net.");
		}
	}
}