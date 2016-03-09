package liber.request.server;

import liber.enumeration.Field;
import liber.request.RequestFromAccountToLiberserver;

public class GetCaptchaImageForDeletionRequest extends RequestFromAccountToLiberserver {
	@Override
	public Field[] goodFields() {
		return new Field[]{Field.captchaImage, Field.imageType};
	}
}
