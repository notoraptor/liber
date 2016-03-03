package liber;

import liber.data.Contact;
import liber.data.Liberaddress;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.exception.LibercardException;
import liber.notification.Notification;
import liber.request.Request;
import liber.request.Response;
import liber.request.client.ContactDataDeletedRequest;
import liber.request.client.ContactDataUpdatedRequest;
import liber.request.server.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
* TODO - FUTURES FONCTIONNALITÉS.
* Actuellement, le service de poste ne prend en charge que les messages de discussion.
* À l'avenir, il faudrait gérer tous les messages échangés entre contacts,
* aussi bien les messages de discussion que les messages d'entretien de la relation.
* Deux possibilités:
* * Généraliser le service de poste pour tous les messages du protocole.
* * Mettre en place un système de "liste de tâches" dans laquelle seraient temporairement entreposées
* * * les opérations qui n'ont pu être réalisées à cause de l'échec de l'envoi de messages.
* * * Ce système permettrait de relancer automatiquement la tâche dès que faisable
* * * (lorsque le contact concerné serait en ligne). Il suffirait d'entreposer les tâches
* * * sous la forme de commandes à exécuter plus tard.
**/

// NOTE: Classe encapsulée (on n'utilise plus Libersaurus.current dans ce fichier).
public class Features {
	private Libersaurus liber;
	public Features(Libersaurus libersaurus) {
		liber = libersaurus;
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
				liber.create(liberaddress, password);
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
				liber.account().confirm();
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
				liber.account().setToDelete();
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
				liber.delete();
				Notification.good("Compte supprimé.");
			} catch (LibercardException e) {
				Notification.bad("Impossible de supprimer la libercarte de ce compte.");
			}
		} catch (Exception e) {
			Notification.bad("Impossible d'envoyer le code CAPTCHA. Êtes-vous connecté à Internet ?");
		}
	}
	public void login(String theLiberaddress, String password) {
		Liberaddress liberaddress = Liberaddress.build(theLiberaddress);
		if (liberaddress != null) try {
			Response response = new LoginRequest(liberaddress, password).justSend();
			if (response != null) if (response.good()) {
				liber.login(liberaddress, password);
			} else switch (response.status()) {
				case "ERROR_ACCOUNT_TO_DELETE":
					Notification.good("liber.Account must be deleted."); // " Use \"validateDeletion\" command.");
					liber.loginToDelete(liberaddress, password);
					break;
				case "ERROR_ACCOUNT_TO_CONFIRM":
					Notification.good("liber.Account must be confirmed."); // " Use \"validateCreation\" command."
					liber.loginToConfirm(liberaddress, password);
					break;
				default:
					Notification.bad("Unable to login\n(" + response.status() + ").");
					break;
			}
		} catch (LibercardException e) {
			Notification.bad("Impossible de charger la libercarte.");
			e.printStackTrace();
		} catch(Exception e) {
			Notification.bad("Impossible de se connecter. Êtes-vous connecté à Internet ?");
		}
	}
	public void logout() {
		liber.logout();
	}
	public void newContact(String liberaddress, String message) {
		Liberaddress recipient = Liberaddress.build(liberaddress);
		if (recipient != null) liber.createOutlink(recipient, message);
	}
	public void cancelOutLink(String liberaddressString) {
		Liberaddress liberaddress = Liberaddress.build(liberaddressString);
		if (liberaddress != null) liber.cancelOutlink(liberaddress);
	}
	public void deleteContact(String liberaddressString) {
		Liberaddress liberaddress = Liberaddress.build(liberaddressString);
		if (liberaddress != null) liber.deleteContact(liberaddress);
	}
	public void clearHistory(String liberaddressString) {
		Liberaddress liberaddress = Liberaddress.build(liberaddressString);
		if (liberaddress != null) liber.clearHistory(liberaddress);
	}
	public void acceptInlink(String liberaddressString) {
		Liberaddress liberaddress = Liberaddress.build(liberaddressString);
		if (liberaddress != null) liber.acceptInlink(liberaddress);
	}
	public void refuseInlink(String liberaddressString) {
		Liberaddress liberaddress = Liberaddress.build(liberaddressString);
		if (liberaddress != null) liber.refuseInlink(liberaddress);
	}
	public void newMessage(String theLiberaddress, String message) {
		Liberaddress liberaddress = Liberaddress.build(theLiberaddress);
		if (liberaddress != null) liber.newMessage(liberaddress, message);
	}
	public void updateInfo(ContactData data, String value) {
		liber.updateInfo(data, value);
		for (Contact contact : liber.contacts()) {
			try {
				new ContactDataUpdatedRequest(contact, data, value).justSend();
			} catch (Exception ignored) {}
		}
		Notification.good(data + " updated.");
	}
	public void deleteInfo(ContactData data) {
		liber.deleteInfo(data);
		for (Contact contact : liber.contacts()) {
			try {
				new ContactDataDeletedRequest(contact, data).justSend();
			} catch (Exception ignored) {
			}
		}
		Notification.good(data + " deleted.");
	}
	public byte[] getCaptchaImageForCreation() {
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
	public byte[] getCaptchaImageForDeletion() {
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
	public byte[] getCaptchaImage(String captchaImage) {
		return Base64.getDecoder().decode(captchaImage);
	}
	public void generateCaptchaImageForCreation(String captchaImage, String imageType) throws IOException {
		generateCaptchaImage(captchaImage, imageType, "Creation");
	}
	public void generateCaptchaImageForDeletion(String captchaImage, String imageType) throws IOException {
		generateCaptchaImage(captchaImage, imageType, "Deletion");
	}
	private void generateCaptchaImage(String captchaImage, String imageType, String imageName) throws IOException {
		byte[] buffer = Base64.getDecoder().decode(captchaImage);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(buffer));
		File file = new File("captchaImageFor" + imageName + "." + imageType);
		ImageIO.write(image, imageType, file);
		System.out.println("[L'image CAPTCHA est dans le fichier \"" + file.getAbsolutePath() + "\"].");
	}
}