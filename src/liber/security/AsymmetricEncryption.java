package liber.security;

import liber.request.requestSent.Request;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class AsymmetricEncryption {
	private RSAPrivateKey privateKey;
	private RSAPublicKey publicKey;
	private boolean keysAreGenerated;
	public AsymmetricEncryption() throws NoSuchProviderException, NoSuchAlgorithmException {
		// debug
		System.err.println("Création de nouvelles clés de chiffrement.");
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
		kpg.initialize(2048);
		KeyPair kp = kpg.genKeyPair();
		privateKey = (RSAPrivateKey) kp.getPrivate();
		publicKey = (RSAPublicKey) kp.getPublic();
		keysAreGenerated = true;
	}
	public AsymmetricEncryption(Object privateKeyObject, Object publicKeyObject) throws Exception {
		// debug
		System.err.println("Chargement de clés de chiffrement.");
		privateKey = (RSAPrivateKey) Secure.getPrivateKey(privateKeyObject);
		publicKey = (RSAPublicKey) Secure.getPublicKey(publicKeyObject);
		if(privateKey == null)
			throw new Exception("Impossible de charger une clé privée.");
		if(publicKey == null)
			throw new Exception("Impossible de charger une clé publique.");
		keysAreGenerated = false;
	}
	public boolean keysAreGenerated() {
		return keysAreGenerated;
	}
	public String publicKeyToString() {
		return PemFormat.publicKey(publicKey).write().toString();
	}
	public String privateKeyToString() {
		return PemFormat.privateKey(privateKey).write().toString();
	}
	public byte[] publicKeyStringBytes() {
		return PemFormat.publicKey(publicKey).write().toString().getBytes();
	}
	public StringBuilder encrypt(Request request) throws Exception {
		return Secure.encryptWithAESAndRSA(request, publicKey);
	}
	public StringBuilder decrypt(StringBuilder content) throws Exception {
		StringBuilder decrypted = null;
		byte[] decryptedContent = Secure.decryptWithAESAndRSA(content, privateKey);
		if(decryptedContent != null) {
			decrypted = new StringBuilder();
			decrypted.append(new String(decryptedContent));
		}
		return decrypted;
	}
}

