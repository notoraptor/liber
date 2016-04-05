package liber.security;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.StringWriter;
import java.security.Key;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

class PemFormat {
	private PemObject pemObject;
	private PemFormat (Key key, String description) {
		this.pemObject = new PemObject(description, key.getEncoded());
	}
	public StringWriter write() {
		try (
			StringWriter stringWriter = new StringWriter();
			PemWriter pemWriter = new PemWriter(stringWriter)
		) {
			pemWriter.writeObject(this.pemObject);
			return stringWriter;
		} catch (Exception e) {
			return null;
		}
	}
	static public PemFormat publicKey(RSAPublicKey key) {
		return new PemFormat(key, "PUBLIC KEY");
	}
	static public PemFormat privateKey(RSAPrivateKey key) {
		return new PemFormat(key, "PRIVATE KEY");
	}
}
