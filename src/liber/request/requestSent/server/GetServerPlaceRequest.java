package liber.request.requestSent.server;

import liber.Libersaurus;
import liber.data.Liberaddress;
import liber.enumeration.Field;
import liber.request.requestSent.RequestFromCandidateToLiberserver;
import liber.request.requestSent.RequestName;

public class GetServerPlaceRequest extends RequestFromCandidateToLiberserver {
	public GetServerPlaceRequest(Liberaddress liberaddress) {
		super(liberaddress.liberserver());
		add(Field.username, liberaddress.username());
		add(Field.publicIP, Libersaurus.current.publicIP());
	}
	@Override
	public Field[] goodFields() {
		return new Field[]{Field.ip, Field.port};
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.getServerPlace;
	}
}
