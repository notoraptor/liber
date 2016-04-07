package liber;

import liber.card.Libercard;
import liber.card.LibercardReport;
import liber.data.*;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.exception.*;
import liber.internet.InternetDependant;
import liber.internet.InternetLookup;
import liber.notification.Notification;
import liber.notification.info.*;
import liber.request.requestReceived.ReceivedRequest;
import liber.request.requestSent.Request;
import liber.request.Response;
import liber.request.requestSent.client.*;
import liber.request.requestSent.server.*;
import liber.security.Secure;
import liber.server.DistantServer;
import liber.server.ServerInterface;

import java.io.*;
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
	static private final String fatalMessage = "Impossible de communiquer avec votre liber-serveur.\n" +
			"Êtes-vous connecté à Internet?\n" +
			"La procédure de connexion reprendra automatiquement lorsqu'une connexion à Internet sera détectée.";
	static public Libersaurus current;
	private File directory;
	private Configuration configuration;
	private ServerInterface server;
	private Features features;
	private Libercard libercard;
	private InternetLookup internetLookup;
	public Libersaurus() throws Exception {
		Secure.init();
		directory = Utils.workingDirectory();
		configuration = new Configuration(directory);
		/*
		try {
			System.err.println("Création d'un serveur local joignable.");
			server = new Server(configuration.getPrivatePort(), configuration.getPublicPort());
			configuration.setPorts(server.privatePort(), server.publicPort());
		} catch (Exception e) {
			System.err.println("Impossible de créer un serveur local joignable: " + e);
			System.err.println("Utilisation du liber-serveur (dès que possible) comme serveur distant.");
			server = new DistantServer(this);
		}
		*/
		/**/
		System.err.println("Utilisation par défaut d'un serveur distant.");
		server = new DistantServer(this);
		/**/
		server.start();
		libercard = null;
		internetLookup = null;
		features = new Features(this);
		current = this;
	}
	private void clearLibercard() throws Exception {
		if(libercard != null) {
			libercard.save();
			libercard = null;
		}
	}
	@Override
	public void close() {
		if(online())
			features().logout();
		try {
			clearLibercard();
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
	private void stopInternetLookup() {
		if(internetLookup.isAlive()) try {
			internetLookup.join();
		} catch(InterruptedException ignored) {}
		internetLookup = null;
	}
	@Override
	public void connect() {
		if(internetLookup != null) {
			stopInternetLookup();
			if (loaded()) reLogin();
		}
	}
	@Override
	public void disconnect() {
		if(internetLookup != null) {
			String previousAddress = internetLookup.address();
			stopInternetLookup();
			lookupInternet(previousAddress);
		}
	}
	public File getDirectory() {
		return directory;
	}
	private void reLogin() {
		finalizeLogin();
	}
	private void load(Liberaddress liberaddress, String password) throws LibercardException {
		try {
			libercard = Libercard.load(liberaddress, password);
			if (libercard == null) {
				create(liberaddress, password);
			} else {
				System.err.println("[Libercarte chargée.]");
			}
		} catch (Exception e) {
			throw new LibercardException(e);
		}
	}
	private void finalizeLogin() {
		// Récupérer les requêtes en attente pour ce compte sur son liber-serveur.
		if(!(server instanceof DistantServer)) while(getNextPosted());
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
			if (Request.sendRequest(new NextPostedMessageReceivedRequest(), fatalMessage) == null)
				return;
			try {
				StringBuilder requestContent = libercard.account.decrypt(response.get(Field.requestBody));
				try (BufferedReader in = new BufferedReader(new StringReader( requestContent.toString()))) {
					ReceivedRequest receivedRequest = ReceivedRequest.parse(in);
					receivedRequest.respond();
					Contact contact = getContact(receivedRequest.sender());
					if (contact != null) {
						long microtime = Long.parseLong(receivedRequest.get(Field.microtime));
						InMessage message = contact.getInMessage(microtime);
						Response mar = Request.sendRequest(new MessageAcknowledgmentRequest(contact, microtime));
						if (mar == null || mar.bad()) {
							contact.addToAcknowledgeLater(message);
						}
						senders.add(contact);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			Notification.good("Vous avez reçu des messages de " + s + '.');
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
		new Thread(() -> {
			for (Contact contact : contacts()) try {
				new NowOnlineRequest(contact).justSend();
			} catch (Exception ignored) {} // Le contact est peut-être déconnecté.
		}).start();
		//Notification.good("Connexion réussie.");
	}
	private void finalizeLogout() throws Exception {
		clearLibercard();
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
		return libercard.password;
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
	// Autre ...
	public boolean getNextPosted() {
		boolean received = false;
		synchronized (this) {
			if(online()) {
				try {
					Response response = new GetNextPostedRequest().justSend();
					if (response.good()) {
						received = true;
						new NextPostedReceivedRequest().justSend();
						//String sender = response.get(Field.sender);
						StringBuilder requestContent = libercard.account.decrypt(response.get(Field.requestBody));
						try (BufferedReader in = new BufferedReader(new StringReader(requestContent.toString()))) {
							ReceivedRequest receivedRequest = ReceivedRequest.parse(in);
							System.err.println("<<\t" + receivedRequest.name());
							receivedRequest.respond();
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return received;
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
	public void setContactOnline(Liberaddress sender, String secret) throws RequestException {
		Contact contact = libercard.contacts.get(sender);
		if (contact != null && contact.secretIs(secret)) {
			contact.setOnline();
			try {
				Response r = new NowOnlineAcknowledgmentRequest(contact).justSend();
				if(r.good()) libercard.account.sentInfosToContact(contact);
			} catch (Throwable ignored) {}
			contact.manageOnline();
			//Notification.good("Le contact " + contact.appellation() + " est connecté.");
			Notification.info(new ContactUpdated(contact));
		} else throw RequestException.ERROR_CONTACT();
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
		if (invitation == null) throw RequestException.ERROR_OUTLINK_UNKNOWN();
		Contact contact = invitation.recipient();
		if (!contact.secretIs(secret)) throw RequestException.ERROR_SECRET();
		libercard.outlinks.remove(invitation);
		libercard.contacts.add(contact);
		contact.addMessage(invitation);
		invitation.setSent();
		Notification.good(contact.appellation() + " a accepté votre demande de mise en relation!");
		Notification.info(new OutlinkAccepted(invitation));
	}
	public void addInlink(Liberaddress sender, String secret, long microtime, String invitation) throws RequestException {
		if (libercard.contacts.has(sender)) throw RequestException.ERROR_INLINK();
		if (libercard.inlinks.has(sender)) throw RequestException.ERROR_INLINK();
		if (libercard.outlinks.has(sender)) throw RequestException.ERROR_INLINK();
		Contact contact;
		try {
			contact = new Contact(sender, secret, null);
		} catch (Exception e) {
			throw RequestException.ERROR_INLINK();
		}
		if(!contact.exists())
			throw RequestException.ERROR_USER_ADDRESS();
		InMessage inlink = new InMessage(contact, microtime, invitation);
		libercard.inlinks.add(inlink);
		Notification.good("Vous avez reçu une demande de contact de la part de " + contact.appellation() + '.');
		Notification.info(new LinkOfferReceived(inlink));
	}
	public void addMessage(Liberaddress sender, String secret, long microtime, String content) throws RequestException {
		Contact contact = libercard.contacts.get(sender);
		if (contact == null) throw RequestException.ERROR_CONTACT();
		if (!contact.secretIs(secret)) throw RequestException.ERROR_SECRET();
		InMessage message = new InMessage(contact, microtime, content);
		contact.addMessage(message);
	}
	public String getMessageHash(Liberaddress sender, String secret, long microtime) throws RequestException {
		Contact contact = libercard.contacts.get(sender);
		if (contact == null) throw RequestException.ERROR_CONTACT();
		if (!contact.secretIs(secret)) throw RequestException.ERROR_SECRET();
		OutMessage message = contact.getOutMessage(microtime);
		if (message == null) throw RequestException.ERROR_NO_MESSAGE();
		try {
			return message.getHash();
		} catch (HashException e) {
			throw RequestException.ERROR_HASH();
		}
	}
	public void aknowledgeMessage(Liberaddress sender, String secret, long microtime) throws RequestException {
		Contact contact = libercard.contacts.get(sender);
		if (contact == null) throw RequestException.ERROR_CONTACT();
		if (!contact.secretIs(secret)) throw RequestException.ERROR_SECRET();
		OutMessage message = contact.getOutMessage(microtime);
		if (message == null || (!message.isConfirmationWaiting() && !message.isLiberserverWaiting()))
			throw RequestException.ERROR_NO_MESSAGE();
		message.setSent();
		Notification.info(new OutMessageUpdated(message));
	}
	public void checkContactDeletion(Liberaddress sender, String secret) throws RequestException {
		Contact contact = libercard.contacts.get(sender);
		if (contact == null) throw RequestException.ERROR_CONTACT();
		if (!contact.secretIs(secret)) throw RequestException.ERROR_SECRET();
		libercard.deleteContact(contact);
		Notification.contactDeleted(contact);
		Notification.info(new ContactDeleted(contact));
	}
	// Action.
	public void create(Liberaddress liberaddress, String password) throws Exception {
		libercard = new Libercard(liberaddress, password);
		Notification.good("Libercarte créée.");
	}
	public void login(Liberaddress liberaddress, String password) throws LibercardException {
		load(liberaddress, password);
		libercard.account.confirm();
		try {
			if (libercard.account.keysAreGenerated()) {
				Response response = new SetPublicKeyRequest().justSend();
				if (response.bad())
					throw new Exception("Impossible d'envoyer la clé sur le liberserveur.");
				System.err.println("Clé de chiffrement envoyée sur le liberserveur.");
			}
		} catch (Exception e) {
			throw new LibercardException(e);
		}
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
		synchronized (this) {
			if(online()) {
				// Notifier tous les contacts de la déconnexion (ignorer les erreurs ici).
				for (Contact contact : contacts()) if(contact.online()) try {
					new NowOfflineRequest(contact).justSend();
				} catch (Exception ignored) {}
				// Transférer les messages en attente vers leurs liber-serveurs respectifs.
				for (Contact contact : contacts())
					contact.transferLocationWaitingMessagesToServers();
			}
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
	}
	public void delete() throws LibercardException {
		if (libercard != null) {
			for(Contact contact: contacts()) if(contact.online()) try {
				new LinkDeletedRequest(contact).justSend();
			} catch(Exception ignored) {}
			libercard.delete();
			libercard = null;
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
				TODO: il faut aussi s'occuper des waitingMessages
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
		if (message == null) Notification.bad("Vous n'avez pas reçu de demande de cette liber-adresse.");
		else {
			Response response = Request.sendRequest(new LinkAcceptedRequest(message.sender()));
			if (response != null) if (response.bad()) {
				Notification.bad("Impossible d'informer l'utilisateur que vous avez accepté sa demande." +
						" Veuillez réessayer plus tard!");
			} else {
				Contact contact = message.sender();
				libercard.inlinks.remove(message);
				libercard.contacts.add(contact);
				contact.addNotInformedMessage(message);
				Request.sendRequest(new NowOnlineRequest(contact));
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
			if(potentialContact.exists()) {
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
			} else {
				Notification.bad("Impossible de localiser cet utilisateur sur Internet.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Notification.bad("Impossible d'envoyer la demande de mise en relation.\n" + e.getMessage());
		}
	}
	public void setContactWatchMe(Liberaddress sender, String secret, String waitingMessages) {
		Contact contact = libercard.contacts.get(sender);
		if(contact != null && contact.secretIs(secret)) {
			contact.setOnline();
			Notification.info(new ContactUpdated(contact));
			int m = Integer.valueOf(waitingMessages);
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
			libercard.account.sentInfosToContact(contact);
		}
	}
	public LibercardReport reportLibercard() {
		return libercard.report();
	}
	public void validateCreation() throws Exception {
		libercard.account.confirm();
		System.err.println("Clés " + (libercard.account.keysAreGenerated() ? "créées" : "chargées") + '.');
		if(libercard.account.keysAreGenerated()) {
			Response response = new SetPublicKeyRequest().justSend();
			if(response.bad())
				throw new Exception("Impossible d'envoyer la clé sur le liberserveur.");
			System.err.println("Clé de chiffrement envoyée sur le liberserveur.");
		}
	}
}