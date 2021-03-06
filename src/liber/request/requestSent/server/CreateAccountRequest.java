package liber.request.requestSent.server;

import liber.Libersaurus;
import liber.data.Liberaddress;
import liber.enumeration.Field;
import liber.request.requestSent.RequestFromCandidateToLiberserver;
import liber.request.requestSent.RequestName;

public class CreateAccountRequest extends RequestFromCandidateToLiberserver {
	public CreateAccountRequest(Liberaddress liberaddress, String password) {
		super(liberaddress.liberserver());
		add(Field.username, liberaddress.username());
		add(Field.password, password);
		add(Field.publicIP, Libersaurus.current.publicIP());
		add(Field.publicPort, Libersaurus.current.publicPort());
		add(Field.privateIP, Libersaurus.current.privateIP());
		add(Field.privatePort, Libersaurus.current.privatePort());
	}
	@Override
	public Field[] goodFields() {
		return new Field[]{Field.captchaImage, Field.imageType};
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.createAccount;
	}
}
