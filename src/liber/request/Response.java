package liber.request;

import liber.enumeration.ErrorCode;
import liber.enumeration.Field;
import liber.exception.RequestException;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class Response {
	private HashMap<String, String> content;
	public Response(String status) {
		content = new HashMap<>();
		content.put("status", status);
	}
	public Response() {
		this("OK");
	}
	public Response(ErrorCode error) {
		this(error.toString());
	}
	public Response(HashMap<String, String> responseContent) throws RequestException {
		content = responseContent;
		if (!content.containsKey("status"))
			throw RequestException.RESPONSE_ERROR_MISSING_STATUS;
	}
	static public Response parse(BufferedReader in) throws Exception {
		HashMap<String, String> content = new HashMap<>();
		String line;
		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (!line.isEmpty()) {
				if (line.equals("end")) break;
				String[] pieces = line.split("\t", 2);
				if (pieces.length != 2)
					content.put(pieces[0].trim(), null);
				else
					content.put(pieces[0].trim(), pieces[1].trim());
			}
		}
		return new Response(content);
	}
	public void add(String key, String value) {
		content.put(key, value);
	}
	public void add(Field field, String value) {
		content.put(field.toString(), value);
	}
	public boolean has(String key) {
		return content.containsKey(key);
	}
	public boolean has(Field field) {
		return content.containsKey(field.toString());
	}
	public String get(String key) {
		return content.get(key);
	}
	public String get(Field field) {
		return content.get(field.toString());
	}
	public String status() {
		return content.get("status");
	}
	public boolean good() {
		return content.get("status").equals("OK");
	}
	public boolean bad() {
		return !good();
	}
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Map.Entry<String, String> entry : content.entrySet()) {
			String value = entry.getValue();
			if(value == null) value = "";
			s.append(entry.getKey()).append("\t").append(value).append("\r\n");
		}
		s.append("end\r\n");
		return s.toString();
	}
}
