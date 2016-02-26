package liber.request.server;

import liber.enumeration.Field;
import liber.request.RequestFromAccountToLiberserver;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class GetCaptchaImageForDeletionRequest extends RequestFromAccountToLiberserver {
	@Override
	public Field[] goodFields() {
		return new Field[]{Field.captchaImage, Field.imageType};
	}
}
