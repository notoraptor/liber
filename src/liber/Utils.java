package liber;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Utils {
	// Transformation Base64 formel -> Base64 adapté pour les URLs.
	// + -> -
	// / -> _
	// = -> # (transformation totale Base64 formel -> Base64 adapté pour les URLs)
	static final public Charset UTF8 = StandardCharsets.UTF_8;
	static final private Pattern urlPattern = Pattern.compile("^[A-Za-z]+://[A-Za-z0-9/_\\.\\$#-]+$");
	static final private Pattern usernamePattern = Pattern.compile("^[A-Za-z0-9_\\.-]+$");
	static public boolean urlIsValid(String url) {
		return url != null && urlPattern.matcher(url).find();
	}
	static public boolean usernameIsValid(String un) {
		return un != null && usernamePattern.matcher(un).find();
	}
	static public boolean urlIsInvalid(String url) {
		return url == null || !urlPattern.matcher(url).find();
	}
	static public boolean usernameIsInvalid(String un) {
		return un == null || !usernamePattern.matcher(un).find();
	}
	static public byte[] decodeFromURLToBytes(String encoded) {
		encoded = encoded.replace('-', '+');
		encoded = encoded.replace('_', '/');
		return Base64.getDecoder().decode(encoded);
	}
	static public byte[] decodeFromURLToBytes(StringBuilder content) {
		String encoded = content.toString().replace('-', '+');
		encoded = encoded.replace('_', '/');
		return Base64.getDecoder().decode(encoded);
	}
	static public byte[] decodeToBytes(String encoded) {
		return Base64.getDecoder().decode(encoded.getBytes());
	}
	static public byte[] decodeToBytes(StringBuilder encoded) {
		return Base64.getDecoder().decode(encoded.toString().getBytes());
	}
	static public String decodeString(String encoded) {
		return encoded == null ? null : new String(Base64.getDecoder().decode(encoded));
	}
	static public String decodeText(String encoded) {
		return encoded == null ? null : new String(Base64.getDecoder().decode(encoded), UTF8);
	}
	static public String encodeFullyForURL(byte[] bytes) {
		String encoded = Base64.getEncoder().encodeToString(bytes);
		encoded = encoded.replace('+', '-');
		encoded = encoded.replace('/', '_');
		encoded = encoded.replace('=', '#');
		return encoded;
	}
	static public String encodeForURL(byte[] bytes) {
		String encoded = Base64.getEncoder().encodeToString(bytes);
		encoded = encoded.replace('+', '-');
		encoded = encoded.replace('/', '_');
		return encoded;
	}
	static public String encodeBytes(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}
	static public String encodeString(String decoded) {
		return Base64.getEncoder().encodeToString(decoded.getBytes());
	}
	static public String encodeText(String decoded) {
		return Base64.getEncoder().encodeToString(decoded.getBytes(Utils.UTF8));
	}
	static public String hash(String message) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		byte[] hash = md.digest(message.getBytes(Utils.UTF8));
		return encodeFullyForURL(hash);
	}
	static public String hexString(byte[] array) {
		char[] val = new char[2 * array.length];
		String hex = "0123456789ABCDEF";
		for (int i = 0; i < array.length; i++) {
			int b = array[i] & 0xff;
			val[2 * i] = hex.charAt(b >>> 4);
			val[2 * i + 1] = hex.charAt(b & 15);
		}
		return String.valueOf(val);
	}
	static public <K, V> void implode(HashMap<K, V> from, StringBuilder to, String coupleJoiner, String entrySeparator) {
		boolean written = false;
		for (Map.Entry<K, V> entry : from.entrySet()) {
			if (written) to.append(entrySeparator);
			else written = true;
			K key = entry.getKey();
			Object value = entry.getValue();
			if (key != null) {
				if (value == null) value = "";
				to.append(key).append(coupleJoiner).append(value);
			}
		}
	}
	static public <K, V> void implodeForLibercard(HashMap<K, V> from, StringBuilder to) {
		implode(from, to, "=", "\t");
	}
	static public File workingDirectory() throws IOException {
		String directory = System.getProperty("user.home");
		if(directory == null) directory = System.getProperty("user.dir");
		if(directory == null) directory = ".";
		File parent = new File(directory);
		if(!parent.exists())
			throw new IOException("Le dossier de travail parent n'existe pas (" + parent.getAbsolutePath() + ").");
		if(!parent.isDirectory())
			throw new IOException("Le chemin de travail parent ne mène pas à un dossier (" + parent.getAbsolutePath() + ").");
		File file = new File(parent, ".libersaurus/");
		if(!file.exists()) {
			if(!file.mkdir())
				throw new IOException("Impossible de créer le dossier de travail (" + file.getAbsolutePath() + ").");
		}
		if(!file.exists())
			throw new IOException("Le dossier de travail n'existe pas et est impossible à créer (" + file.getAbsolutePath() + ").");
		if(!file.isDirectory())
			throw new IOException("Le chemin de travail ne mène pas à un dossier (" + file.getAbsolutePath() + ").");
		System.out.println("Dossier de travail: " + file.getAbsolutePath());
		return file;
	}
}
