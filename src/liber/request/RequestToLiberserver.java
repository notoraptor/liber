package liber.request;

import liber.data.Liberaddress;
import liber.enumeration.Field;
import liber.recipient.Liberserver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public abstract class RequestToLiberserver extends Request {
	public RequestToLiberserver(Liberaddress senderAddress, Liberserver liberserver) {
		super(senderAddress, liberserver);
	}
	public RequestToLiberserver(Liberserver liberserver) {
		super(liberserver);
	}
	/* Note: nombreuses améliorations possibles pour l'encodage/décodage des requêtes entre client et serveur.
	1) Si la requête est encodée par le client, elle doit être décodée par le serveur avec le même système de codage.
		Par exemple, encodage en URL => décodage en URL chez le serveur (autre emplacement OU LIBER-SERVEUR).
	2) Le codage en Base 64 est peut-être préférable au codage en URL.
	* */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		try {
			s.append("request=").append(URLEncoder.encode(name(), "UTF-8"));
			s.append("&sender=").append(URLEncoder.encode(sender(), "UTF-8"));
			s.append("&recipient=").append(URLEncoder.encode(recipientAddress(), "UTF-8"));
			for (Map.Entry<Field, String> entry : parameters()) {
				String value = entry.getValue();
				if (value == null) value = "";
				s.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(value, "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			s.append("request=").append(name());
			s.append("&sender=").append(sender());
			s.append("&recipient=").append(recipientAddress());
			for (Map.Entry<Field, String> entry : parameters()) {
				String value = entry.getValue();
				if (value == null) value = "";
				s.append("&").append(entry.getKey()).append("=").append(value);
			}
		}
		return s.toString();
	}
}
