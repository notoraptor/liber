package liber.request;

import liber.recipient.Liberserver;

/**
 * Created by HPPC on 21/02/2016.
 */
public abstract class RequestFromCandidateToLiberserver extends RequestToLiberserver {
	public RequestFromCandidateToLiberserver(Liberserver liberserver) {
		super(liberserver);
	}
}
