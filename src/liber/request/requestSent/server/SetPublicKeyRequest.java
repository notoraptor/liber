package liber.request.requestSent.server;

import liber.Libersaurus;
import liber.Utils;
import liber.enumeration.Field;
import liber.request.requestSent.RequestFromAccountToLiberserver;
import liber.request.requestSent.RequestName;

public class SetPublicKeyRequest extends RequestFromAccountToLiberserver {
	public SetPublicKeyRequest() {
		super();
		add(
			Field.publicKey,
			Utils.encodeForURL(Libersaurus.current.account().publicKeyStringBytes())
		);
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.setPublicKey;
	}
}
