package liber.security;

import liber.Utils;
import liber.data.User;
import liber.enumeration.Field;
import liber.request.Response;
import liber.request.requestSent.Request;
import liber.request.requestSent.server.GetPublicKeyRequest;

import java.security.interfaces.RSAPublicKey;

public class AsymmetricPublicKey {
	private RSAPublicKey publicKey;
	public AsymmetricPublicKey(User user) throws Exception {
		Response response = new GetPublicKeyRequest(user).justSend();
		if(response.bad()) {
			System.err.println(response);
			throw new Exception("Impossible d'obtenir la clé publique de l'utilisateur " + user.liberaddress() + '.');
		}
		String pub = new String(Utils.decodeFromURLToBytes(response.get(Field.publicKey)));
		publicKey = (RSAPublicKey) Secure.getPublicKey(pub);
		if(publicKey == null)
			throw new Exception("Impossible de charger la clé publique récupérée d'un utilisateur.");
	}
	public AsymmetricPublicKey(String publicKeyString) throws Exception {
		publicKey = (RSAPublicKey) Secure.getPublicKey(publicKeyString);
		if(publicKey == null)
			throw new Exception("Impossible de charger la clé publique d'un utilisateur.");
	}
	public StringBuilder encrypt(Request request) throws Exception {
		return Secure.encryptWithAESAndRSA(request, publicKey);
	}
	public String publicKeyToString() {
		return PemFormat.publicKey(publicKey).write().toString();
	}
}
