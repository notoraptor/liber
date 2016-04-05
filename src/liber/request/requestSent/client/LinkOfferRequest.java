package liber.request.requestSent.client;

import liber.data.OutMessage;
import liber.enumeration.Field;
import liber.request.requestSent.RequestName;
import liber.request.requestSent.RequestToLiberaddress;

public class LinkOfferRequest extends RequestToLiberaddress {
	public LinkOfferRequest(OutMessage outlink) {
		super(outlink.recipient());
		add(Field.microtime, outlink.microtime());
		add(Field.invitation, outlink.encodedContent());
	}
	@Override
	protected RequestName getRequestName() {
		return RequestName.linkOffer;
	}
}
