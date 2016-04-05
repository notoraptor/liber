package liber.security;

import liber.Utils;
import liber.request.requestSent.Request;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class Secure {
	static private boolean notInitiated = true;
	static public void init() {
		if(notInitiated) {
			java.security.Security.addProvider(new BouncyCastleProvider());
			notInitiated = false;
		}
	}
	static public PublicKey getPublicKey(Object pemFormatKeyString) {
		PublicKey key = null;
		int keyCount = 0;
		if(pemFormatKeyString != null) {
			Reader reader = new StringReader(pemFormatKeyString.toString());
			PEMParser parser = new PEMParser(reader);
			Object o;
			try {
				while ((o = parser.readObject()) != null) {
					if (o instanceof SubjectPublicKeyInfo) {
						JcaPEMKeyConverter myConverter = new JcaPEMKeyConverter();
						RSAPublicKey myKey = (RSAPublicKey) myConverter.getPublicKey((SubjectPublicKeyInfo) o);
						key = myKey;
						++keyCount;
					}
				}
			} catch (IOException ignored) {}
		}
		if(keyCount != 1) key = null;
		return key;
	}
	static public PrivateKey getPrivateKey(Object pemFormatKeyString) {
		PrivateKey key = null;
		int keyCount = 0;
		if(pemFormatKeyString != null) {
			Reader reader = new StringReader(pemFormatKeyString.toString());
			PEMParser parser = new PEMParser(reader);
			Object o;
			try {
				while ((o = parser.readObject()) != null) {
					if (o instanceof PrivateKeyInfo) {
						JcaPEMKeyConverter myConverter = new JcaPEMKeyConverter();
						RSAPrivateKey myKey = (RSAPrivateKey) myConverter.getPrivateKey((PrivateKeyInfo) o);
						key = myKey;
						++keyCount;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(keyCount != 1) key = null;
		return key;
	}
	static public StringBuilder encryptWithAESAndRSA(Request request, PublicKey RSAKey) throws Exception {
		// Initialisation pour RSA.
		Cipher RSACipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
		RSACipher.init(Cipher.ENCRYPT_MODE, RSAKey);
		// Génération de la clé AES.
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		SecretKey AESKey = keyGen.generateKey();
		SecretKeySpec aesKeySpec = new SecretKeySpec(AESKey.getEncoded(), "AES");
		// Initialisation pour AES.
		Cipher AESCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
		AESCipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
		byte[] iv = AESCipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
		String aesString = Utils.encodeForURL(AESKey.getEncoded());
		String ivString = Utils.encodeForURL(iv);
		String aesKeyInfoString = aesString + ';' + ivString;
		// Chiffrement de la clé AES avec la clé RSA et du contenu avec la clé AES.
		StringBuilder finalString = new StringBuilder();
		// Clé.
		finalString.append(Utils.encodeForURL(RSACipher.doFinal(aesKeyInfoString.getBytes())));
		// Contenu chiffré.
		finalString.append(';');
		finalString.append(Utils.encodeForURL(AESCipher.doFinal(request.toString().getBytes())));
		//System.err.println("[encrypted] " + finalString);
		return finalString;
	}
	static public byte[] decryptWithAESAndRSA(StringBuilder content, PrivateKey RSAKey) throws Exception {
		//System.err.println("[toDecrypt] " + content);
		byte[] decrypted = null;
		String[] pieces = content.toString().split(";");
		if(pieces.length == 2) {
			String encryptedWithRSA = pieces[0];
			String encryptedWithAES = pieces[1];
			Cipher RSACipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
			RSACipher.init(Cipher.DECRYPT_MODE, RSAKey);
			String AESKeyInfos = new String(RSACipher.doFinal(Utils.decodeFromURLToBytes(encryptedWithRSA)));
			String[] AESKeyInfoPieces = AESKeyInfos.split(";");
			if(AESKeyInfoPieces.length == 2) {
				byte[] aes = Utils.decodeFromURLToBytes(AESKeyInfoPieces[0]);
				byte[] iv = Utils.decodeFromURLToBytes(AESKeyInfoPieces[1]);
				SecretKeySpec aesKeySpec = new SecretKeySpec(aes, "AES");
				IvParameterSpec ivSpec = new IvParameterSpec(iv);
				Cipher AESCipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
				AESCipher.init(Cipher.DECRYPT_MODE, aesKeySpec, ivSpec);
				decrypted = AESCipher.doFinal(Utils.decodeFromURLToBytes(encryptedWithAES));
			}
		}
		return decrypted;
	}
}
