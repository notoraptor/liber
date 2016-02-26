package liber.data;

import liber.Utils;
import liber.exception.HashException;

import java.util.Base64;

/**
 * Created by HPPC on 21/02/2016.
 */
public class MessageContent {
	private String base64content;
	public MessageContent(String content, boolean isEncoded) {
		assert content != null;
		base64content = isEncoded ? content : Base64.getEncoder().encodeToString(content.getBytes());
	}
	public MessageContent(String content) {
		this(content, false);
	}
	public String decode() {
		return new String(Base64.getDecoder().decode(base64content));
	}
	public String getHash() throws HashException {
		try {
			return Utils.hash(base64content);
		} catch (Exception e) {
			throw new HashException(e);
		}
	}
	@Override
	public String toString() {
		return base64content;
	}
	@Override
	public int hashCode() {
		return base64content.hashCode();
	}
	@Override
	public boolean equals(Object o) {
		return (o instanceof MessageContent) && base64content.equals(((MessageContent) o).base64content);
	}
}
