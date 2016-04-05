package liber.command;

import liber.Features;
import liber.Libersaurus;
import liber.notification.Notification;
import liber.enumeration.CommandField;
import liber.enumeration.Field;
import liber.request.Response;
import liber.request.requestSent.server.GetCaptchaImageForDeletionRequest;

import java.io.IOException;

public class ValidateDeletionCommand extends CommandForLoaded {
	private StringBuilder captchaImage;
	private StringBuilder imageType;
	public ValidateDeletionCommand() {
		super();
	}
	private void getCaptchaImage() {
		try {
			Response response = new GetCaptchaImageForDeletionRequest().justSend();
			if (response.good()) {
				captchaImage = response.get(Field.captchaImage);
				imageType = response.get(Field.imageType);
			} else {
				Notification.bad(
						"Impossible de récupérer l'image CAPTCHA pour la suppression du compte (" + response.status() + ").");
			}
		} catch (Exception e) {
			Notification.bad(
					"Impossible de récupérer l'image CAPTCHA pour la suppression du compte. Êtes-vous connecté à Internet?");
		}
	}
	private void generateCaptchaImage() throws IOException {
		Features.generateCaptchaImageForDeletion(captchaImage, imageType);
	}
	@Override
	public CommandField[] fields() {
		return new CommandField[]{CommandField.captchaCode};
	}
	@Override
	public boolean check() {
		if (!super.check()) return false;
		if (!Libersaurus.current.account().toDelete()) {
			Notification.bad("Loaded account has not to be deleted.");
			return false;
		}
		return true;
	}
	@Override
	public boolean analyzeCommandLine() {
		if(!super.analyzeCommandLine()) return false;
		getCaptchaImage();
		if (captchaImage == null) return false;
		try {
			generateCaptchaImage();
			return true;
		} catch (IOException e) {
			Notification.bad("Impossible de générer l'image CAPTCHA pour la suppression du compte.");
			return false;
		}
	}
	@Override
	public void execute() {
		Libersaurus.current.features().validateDeletion(get(CommandField.captchaCode));
	}
}
