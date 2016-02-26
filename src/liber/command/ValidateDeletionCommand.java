package liber.command;

import liber.Libersaurus;
import liber.notification.Notification;
import liber.enumeration.CommandField;
import liber.enumeration.Field;
import liber.request.Response;
import liber.request.server.GetCaptchaImageForDeletionRequest;

import java.io.IOException;

/**
 * Created by HPPC on 21/02/2016.
 */
public class ValidateDeletionCommand extends CommandForLoaded {
	private String captchaImage;
	private String imageType;
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
		Libersaurus.current.features().generateCaptchaImageForDeletion(captchaImage, imageType);
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
