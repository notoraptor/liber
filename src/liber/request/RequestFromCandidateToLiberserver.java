package liber.request;

import liber.recipient.Liberserver;

public abstract class RequestFromCandidateToLiberserver extends RequestToLiberserver {
	public RequestFromCandidateToLiberserver(Liberserver liberserver) {
		super(liberserver);
	}
}
