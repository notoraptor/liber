package liber.request.client;

import liber.data.OutMessage;
import liber.enumeration.Field;
import liber.request.RequestName;
import liber.request.RequestToLiberaddress;

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
