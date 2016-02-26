package liber.request.server;

import liber.Libersaurus;
import liber.data.Liberaddress;
import liber.enumeration.Field;
import liber.recipient.Liberserver;
import liber.request.RequestFromCandidateToLiberserver;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/ // liber.RequestFromCandidateToLiberserver
public class GetServerPlaceRequest extends RequestFromCandidateToLiberserver {
	public GetServerPlaceRequest(Liberserver liberserver, String username) {
		super(liberserver);
		add(Field.username, username);
		add(Field.publicIP, Libersaurus.current.publicIP());
	}
	public GetServerPlaceRequest(Liberaddress liberaddress) {
		this(liberaddress.liberserver(), liberaddress.username());
	}
	@Override
	public Field[] goodFields() {
		return new Field[]{Field.ip, Field.port};
	}
}
