package liber.command;

import liber.Libersaurus;
import liber.notification.Notification;
import liber.enumeration.CommandField;
import liber.enumeration.Field;
import liber.request.Response;
import liber.request.server.GetCaptchaImageForCreationRequest;

import java.io.IOException;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class ValidateCreationCommand extends CommandForLoaded {
	private String captchaImage;
	private String imageType;
	public ValidateCreationCommand() {
		super();
	}
	private void getCaptchaImage() {
		try {
			Response response = new GetCaptchaImageForCreationRequest().justSend();
			if (response.good()) {
				captchaImage = response.get(Field.captchaImage);
				imageType = response.get(Field.imageType);
			} else {
				Notification.bad("Impossible de récupérer l'image CAPTCHA pour la création du compte (" + response.status() + ").");
			}
		} catch (Exception e) {
			Notification.bad("Impossible de récupérer l'image CAPTCHA pour la création du compte. Êtes-vous connecté à Internet ?");
		}
	}
	private void generateCaptchaImage() throws IOException {
		Libersaurus.current.features().generateCaptchaImageForCreation(captchaImage, imageType);
	}
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.captchaCode};
	}
	@Override
	public boolean check() {
		if (!super.check()) return false;
		if (!Libersaurus.current.account().toConfirm()) {
			Notification.bad("Loaded account has not to be validated.");
			return false;
		}
		return true;
	}
	@Override
	public boolean checkCommandLine() {
		if (!super.checkCommandLine()) return false;
		String s = get(CommandField.captchaCode);
		return !(s == null || s.isEmpty()) || Notification.bad("Veuillez entrer le code CAPTCHA.");
	}
	@Override
	public boolean analyzeCommandLine() {
		if(!super.analyzeCommandLine()) return false;
		// Comme nous sommes en ligne de commande, il faut générer l'image CAPTCHA dans un fichier.
		getCaptchaImage();
		if (captchaImage == null) return false;
		try {
			generateCaptchaImage();
			return true;
		} catch (IOException e) {
			Notification.bad("Impossible de générer l'image CAPTCHA pour la création du compte.");
			return false;
		}
	}
	@Override
	public void execute() {
		Libersaurus.current.features().validateCreation(get(CommandField.captchaCode));
	}
}
