package liber;

import liber.data.Contact;
import liber.data.Liberaddress;
import liber.enumeration.ContactData;
import liber.enumeration.Field;
import liber.exception.*;
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

// NOTE: Classe bien encapsul�e (on n'utilise plus Libersaurus.current dans ce fichier).
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
				Notification.bad("Impossible de cr�er le compte (" + response.status() + ").");
				Notification.bad("Aviez-vous d�j� cr�� ce compte ? Si oui, essayez de vous connecter (avec la commande \"login\").");
				response = null;
			} else {
				liber.create(liberaddress, password);
				Notification.good("Compte cr��.");
			}
		} catch(Exception e) {
			Notification.bad("Impossible de cr�er le compte. �tes-vous connect� � Internet ?");
		}
		return response;
	}
	public void validateCreation(String captcha) {
		try {
			Response response = new CaptchaForCreationRequest(captcha).justSend();
			if (response.bad()) Notification.bad("Mauvais code CAPTCHA (" + response.status() + ").");
			else {
				liber.account().confirm();
				Notification.good("Compte valid�.");
			}
		} catch(Exception e) {
			Notification.bad("Impossible d'envoyer le code CAPTCHA. �tes-vous connect�s � Internet ?");
		}
	}
	public Response deleteAccount() {
		Response response = Request.sendRequest(
				new DeleteAccountRequest(), "Impossible de supprimer le compte. �tes-vous connect� � Internet ?");
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
				Notification.good("Compte supprim�.");
			} catch (LibercardException e) {
				Notification.bad("Impossible de supprimer la libercarte de ce compte.");
			}
		} catch (Exception e) {
			Notification.bad("Impossible d'envoyer le code CAPTCHA. �tes-vous connect� � Internet ?");
		}
	}
	public void login(String theLiberaddress, String password) {
		try {
			Liberaddress liberaddress = new Liberaddress(theLiberaddress);
			Response response = new LoginRequest(liberaddress, password).justSend();
			switch (response.status()) {
				case "OK":
					liber.login(liberaddress, password);
					break;
				case "ERROR_ACCOUNT_TO_DELETE":
					Notification.good("Ce compte doit �tre supprim�.");
					liber.loginToDelete(liberaddress, password);
					break;
				case "ERROR_ACCOUNT_TO_CONFIRM":
					Notification.good("Ce compte doit �tre confirm�.");
					liber.loginToConfirm(liberaddress, password);
					break;
				default:
					Notification.bad("Impossible de se connecter\n(" + response.status() + ").");
					break;
			}
		} catch (UsernameException|RecipientAddressException e) {
			Notification.bad(e.getMessage());
		} catch(RecipientException|RequestException e) {
			Notification.bad("Impossible de se connecter. �tes-vous connect� � Internet ?");
		} catch (LibercardException e) {
			Notification.bad("Impossible de charger la libercarte.");
			e.printStackTrace();
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
		for (Contact contact : liber.contacts()) if(contact.online()) try {
			new ContactDataUpdatedRequest(contact, data, value).justSend();
		} catch (Exception ignored) {}
		Notification.good(data + " updated.");
	}
	public void deleteInfo(ContactData data) {
		liber.deleteInfo(data);
		for (Contact contact : liber.contacts()) if(contact.online()) try {
			new ContactDataDeletedRequest(contact, data).justSend();
		} catch (Exception ignored) {}
		Notification.good(data + " deleted.");
	}
	public static byte[] getCaptchaImageForCreation() {
		try {
			Response response = new GetCaptchaImageForCreationRequest().justSend();
			if (response.good()) {
				return getCaptchaImage(response.get(Field.captchaImage));
			} else {
				Notification.bad("Impossible de r�cup�rer l'image CAPTCHA pour valider le compte (" + response.status() + ").");
			}
		} catch (Exception e) {
			Notification.bad("Impossible de r�cup�rer l'image CAPTCHA pour valider le compte. �tes-vous connect� � Internet?");
		}
		return null;
	}
	public static byte[] getCaptchaImageForDeletion() {
		try {
			Response response = new GetCaptchaImageForDeletionRequest().justSend();
			if (response.good()) {
				return getCaptchaImage(response.get(Field.captchaImage));
			} else {
				Notification.bad("Impossible de r�cup�rer l'image CAPTCHA pour la suppression du compte (" + response.status() + ").");
			}
		} catch (Exception e) {
			Notification.bad("Impossible de r�cup�rer l'image CAPTCHA pour la suppression du compte. �tes-vous connect� � Internet?");
		}
		return null;
	}
	private static byte[] getCaptchaImage(String captchaImage) {
		return Base64.getDecoder().decode(captchaImage);
	}
	public static void generateCaptchaImageForCreation(String captchaImage, String imageType) throws IOException {
		generateCaptchaImage(captchaImage, imageType, "Creation");
	}
	public static void generateCaptchaImageForDeletion(String captchaImage, String imageType) throws IOException {
		generateCaptchaImage(captchaImage, imageType, "Deletion");
	}
	private static void generateCaptchaImage(String captchaImage, String imageType, String imageName) throws IOException {
		byte[] buffer = Base64.getDecoder().decode(captchaImage);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(buffer));
		File file = new File("captchaImageFor" + imageName + '.' + imageType);
		ImageIO.write(image, imageType, file);
		System.out.println("[L'image CAPTCHA est dans le fichier \"" + file.getAbsolutePath() + "\"].");
	}
}