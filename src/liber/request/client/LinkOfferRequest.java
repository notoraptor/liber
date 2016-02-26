package liber.request.client;

import liber.data.OutMessage;
import liber.enumeration.Field;
import liber.request.RequestToLiberaddress;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class LinkOfferRequest extends RequestToLiberaddress {
	public LinkOfferRequest(OutMessage outlink) {
		super(outlink.recipient());
		add(Field.microtime, outlink.microtime());
		add(Field.invitation, outlink.encodedContent());
	}
}
