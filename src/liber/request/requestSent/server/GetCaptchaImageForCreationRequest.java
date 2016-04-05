package liber.request.requestSent.server;

import liber.enumeration.Field;
import liber.request.requestSent.RequestFromAccountToLiberserver;
import liber.request.requestSent.RequestName;

public class GetCaptchaImageForCreationRequest extends RequestFromAccountToLiberserver {
	@Override
	public Field[] goodFields() {
		return new Field[]{Field.captchaImage, Field.imageType};
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.getCaptchaImageForCreation;
	}
}
