package liber.data;

import liber.Utils;
import liber.enumeration.Encoding;
import liber.exception.HashException;

public class MessageContent {
	private String base64content;
	public MessageContent(String content, Encoding encoding) {
		assert content != null && encoding != null;
		if(encoding == Encoding.ENCODED)
			base64content = content;
		else
			base64content = Utils.encodeText(content);
	}
	public String decode() {
		return Utils.decodeText(base64content);
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
