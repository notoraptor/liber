package liber.request.server;

import liber.Libersaurus;
import liber.data.Liberaddress;
import liber.enumeration.Field;
import liber.request.RequestFromCandidateToLiberserver;

public class LoginRequest extends RequestFromCandidateToLiberserver {
	public LoginRequest(Liberaddress liberaddress, String password) {
		super(liberaddress.liberserver());
		add(Field.username, liberaddress.username());
		add(Field.password, password);
		add(Field.publicIP, Libersaurus.current.publicIP());
		add(Field.publicPort, Libersaurus.current.publicPort());
		add(Field.privateIP, Libersaurus.current.privateIP());
		add(Field.privatePort, Libersaurus.current.privatePort());
	}
}
