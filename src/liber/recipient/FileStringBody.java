package liber.recipient;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.StringBody;

class FileStringBody extends StringBody {
	public FileStringBody(String content) {
		super(content, ContentType.DEFAULT_BINARY);
	}
	@Override
	public String getFilename() {
		return System.currentTimeMillis() + ".tmp";
	}
}
