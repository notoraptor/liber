package liber;

import liber.data.Contact;
import liber.data.Liberaddress;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.exception.*;
import liber.notification.Notification;
import liber.request.requestSent.Request;
import liber.request.Response;
import liber.request.requestSent.client.ContactDataDeletedRequest;
import liber.request.requestSent.client.ContactDataUpdatedRequest;
import liber.request.requestSent.server.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

// NOTE: Classe bien encapsulée (on n'utilise plus Libersaurus.current dans ce fichier).
public class Features {
	private Libersaurus libersaurus;
	public Features(Libersaurus libersaurus) {
		this.libersaurus = libersaurus;
	}
	public Response createAccount(String liberserver, String username, String password) {
		Response response = null;
		Liberaddress liberaddress = Liberaddress.build(liberserver, username);
		if (liberaddress != null) try {
			response = new CreateAccountRequest(liberaddress, password).justSend();
			if (response.bad()) {
				Notification.bad("Impossible de créer le compte (" + response.status() + ").");
				Notification.bad("Aviez-vous déjà créé ce compte ? Si oui, essayez de vous connecter (avec la commande \"login\").");
				response = null;
			} else {
				libersaurus.create(liberaddress, password);
				Notification.good("Compte créé.");
			}
		} catch(Exception e) {
			Notification.bad("Impossible de créer le compte. Êtes-vous connecté à Internet ?");
		}
		return response;
	}
	public void validateCreation(String captcha) {
		try {
			Response response = new CaptchaForCreationRequest(captcha).justSend();
			if (response.bad()) Notification.bad("Mauvais code CAPTCHA (" + response.status() + ").");
			else {
				libersaurus.validateCreation();
				Notification.good("Compte validé.");
			}
		} catch(Exception e) {
			Notification.bad("Impossible d'envoyer le code CAPTCHA. Êtes-vous connectés à Internet ?");
		}
	}
	public Response deleteAccount() {
		Response response = Request.sendRequest(
				new DeleteAccountRequest(), "Impossible de supprimer le compte. Êtes-vous connecté à Internet ?");
		if (response != null) {
			if (response.good()) {
				libersaurus.account().setToDelete();
				// Notification.good("Deletion request sent.");
			} else {
				Notification.bad("Impossible de supprimer le compte (" + response.status() + ").");
				response = null;
			}
		}
		return response;
	}
	public void validateDeletion(String captcha) {
		try {
			Response response = new CaptchaForDeletionRequest(captcha).justSend();
			if (response.bad()) Notification.bad("Mauvais code CAPTCHA (" + response.status() + ").");
			else try {
				libersaurus.delete();
				Notification.good("Compte supprimé.");
			} catch (LibercardException e) {
				Notification.bad("Impossible de supprimer la libercarte de ce compte.");
			}
		} catch (Exception e) {
			Notification.bad("Impossible d'envoyer le code CAPTCHA. Êtes-vous connecté à Internet ?");
		}
	}
	public void login(String theLiberaddress, String password) {
		try {
			Liberaddress liberaddress = new Liberaddress(theLiberaddress);
			Response response = new LoginRequest(liberaddress, password).justSend();
			switch (response.status()) {
				case "OK":
					libersaurus.login(liberaddress, password);
					break;
				case "ERROR_ACCOUNT_TO_DELETE":
					Notification.good("Ce compte doit être supprimé.");
					libersaurus.loginToDelete(liberaddress, password);
					break;
				case "ERROR_ACCOUNT_TO_CONFIRM":
					Notification.good("Ce compte doit être confirmé.");
					libersaurus.loginToConfirm(liberaddress, password);
					break;
				default:
					Notification.bad("Impossible de se connecter\n(" + response.status() + ").");
					break;
			}
		} catch (UsernameException|RecipientAddressException e) {
			Notification.bad(e.getMessage());
		} catch(RecipientException|RequestException e) {
			Notification.bad("Impossible de se connecter. Êtes-vous connecté à Internet ?");
		} catch (LibercardException e) {
			Notification.bad("Impossible de charger la libercarte.");
			e.printStackTrace();
		}
	}
	public void logout() {
		libersaurus.logout();
	}
	public void newContact(String liberaddress, String message) {
		Liberaddress recipient = Liberaddress.build(liberaddress);
		if (recipient != null) libersaurus.createOutlink(recipient, message);
	}
	public void cancelOutLink(String liberaddressString) {
		Liberaddress liberaddress = Liberaddress.build(liberaddressString);
		if (liberaddress != null) libersaurus.cancelOutlink(liberaddress);
	}
	public void deleteContact(String liberaddressString) {
		Liberaddress liberaddress = Liberaddress.build(liberaddressString);
		if (liberaddress != null) libersaurus.deleteContact(liberaddress);
	}
	public void clearHistory(String liberaddressString) {
		Liberaddress liberaddress = Liberaddress.build(liberaddressString);
		if (liberaddress != null) libersaurus.clearHistory(liberaddress);
	}
	public void acceptInlink(String liberaddressString) {
		Liberaddress liberaddress = Liberaddress.build(liberaddressString);
		if (liberaddress != null) libersaurus.acceptInlink(liberaddress);
	}
	public void refuseInlink(String liberaddressString) {
		Liberaddress liberaddress = Liberaddress.build(liberaddressString);
		if (liberaddress != null) libersaurus.refuseInlink(liberaddress);
	}
	public void newMessage(String theLiberaddress, String message) {
		Liberaddress liberaddress = Liberaddress.build(theLiberaddress);
		if (liberaddress != null) libersaurus.newMessage(liberaddress, message);
	}
	public void updateInfo(ContactData data, String value) {
		libersaurus.updateInfo(data, value);
		new Thread(() -> {
			for (Contact contact : libersaurus.contacts()) if(contact.online()) try {
				new ContactDataUpdatedRequest(contact, data, value).justSend();
				/*
				TODO: Ne vaut-il pas mieux distribuer (dans un autre processus)
				la mise à jour lorsque la donnée modifiée est une photo ?
				*/
			} catch (Exception ignored) {}
		}).start();
		Notification.good(data + " updated.");
	}
	public void deleteInfo(ContactData data) {
		libersaurus.deleteInfo(data);
		new Thread(() -> {
			for (Contact contact : libersaurus.contacts()) if(contact.online()) try {
				new ContactDataDeletedRequest(contact, data).justSend();
			} catch (Exception ignored) {}
		}).start();
		Notification.good(data + " deleted.");
	}
	public static byte[] getCaptchaImageForCreation() {
		try {
			Response response = new GetCaptchaImageForCreationRequest().justSend();
			if (response.good()) {
				return getCaptchaImage(response.get(Field.captchaImage));
			} else {
				Notification.bad("Impossible de récupérer l'image CAPTCHA pour valider le compte (" + response.status() + ").");
			}
		} catch (Exception e) {
			Notification.bad("Impossible de récupérer l'image CAPTCHA pour valider le compte. Êtes-vous connecté à Internet?");
		}
		return null;
	}
	public static byte[] getCaptchaImageForDeletion() {
		try {
			Response response = new GetCaptchaImageForDeletionRequest().justSend();
			if (response.good()) {
				return getCaptchaImage(response.get(Field.captchaImage));
			} else {
				Notification.bad("Impossible de récupérer l'image CAPTCHA pour la suppression du compte (" + response.status() + ").");
			}
		} catch (Exception e) {
			Notification.bad("Impossible de récupérer l'image CAPTCHA pour la suppression du compte. Êtes-vous connecté à Internet?");
		}
		return null;
	}
	private static byte[] getCaptchaImage(StringBuilder captchaImage) {
		return Utils.decodeToBytes(captchaImage);
	}
	public static void generateCaptchaImageForCreation(StringBuilder captchaImage, StringBuilder imageType) throws IOException {
		generateCaptchaImage(captchaImage, imageType, "Creation");
	}
	public static void generateCaptchaImageForDeletion(StringBuilder captchaImage, StringBuilder imageType)
			throws IOException {
		generateCaptchaImage(captchaImage, imageType, "Deletion");
	}
	private static void generateCaptchaImage(StringBuilder captchaImage, StringBuilder imageType, String imageName)
			throws IOException {
		byte[] buffer = Utils.decodeToBytes(captchaImage);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(buffer));
		File file = new File("captchaImageFor" + imageName + '.' + imageType);
		ImageIO.write(image, imageType.toString(), file);
		System.out.println("L'image CAPTCHA est dans le fichier " + file.getAbsolutePath());
	}
}