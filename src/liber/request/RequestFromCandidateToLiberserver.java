package liber.request;

import liber.recipient.Liberserver;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public abstract class RequestFromCandidateToLiberserver extends RequestToLiberserver {
	public RequestFromCandidateToLiberserver(Liberserver liberserver) {
		super(liberserver);
	}
}
