package liber.request.server;

import liber.enumeration.Field;
import liber.request.RequestFromAccountToLiberserver;

public class CaptchaForDeletionRequest extends RequestFromAccountToLiberserver {
	public CaptchaForDeletionRequest(String captcha) {
		super();
		add(Field.captcha, captcha);
	}
	@Override
	public Field[] badFields() {
		return new Field[]{Field.captchaImage, Field.imageType};
	}
}
