package liber.request.requestSent.server;

import liber.data.User;
import liber.enumeration.Field;
import liber.request.requestSent.RequestFromCandidateToLiberserver;
import liber.request.requestSent.RequestName;

public class GetPublicKeyRequest extends RequestFromCandidateToLiberserver {
	public GetPublicKeyRequest(User user) {
		super(user.liberserver());
		add(Field.username, user.username());
	}
	@Override
	public Field[] goodFields() {
		return new Field[] {Field.publicKey};
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.getPublicKey;
	}
}
