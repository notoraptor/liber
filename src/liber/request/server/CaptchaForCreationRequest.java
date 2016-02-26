package liber.request.server;

import liber.enumeration.Field;
import liber.request.RequestFromAccountToLiberserver;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class CaptchaForCreationRequest extends RequestFromAccountToLiberserver {
	public CaptchaForCreationRequest(String captcha) {
		super();
		add(Field.captcha, captcha);
	}
	@Override
	public Field[] badFields() {
		return new Field[]{Field.captchaImage, Field.imageType};
	}
}
