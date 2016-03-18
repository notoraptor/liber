package liber.request.server;

import liber.enumeration.Field;
import liber.request.RequestFromAccountToLiberserver;
import liber.request.RequestName;

public class CaptchaForCreationRequest extends RequestFromAccountToLiberserver {
	public CaptchaForCreationRequest(String captcha) {
		super();
		add(Field.captcha, captcha);
	}
	@Override
	public Field[] badFields() {
		return new Field[]{Field.captchaImage, Field.imageType};
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.captchaForCreation;
	}
}
