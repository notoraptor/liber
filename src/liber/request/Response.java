package liber.request;

import liber.enumeration.Field;
import liber.exception.RequestException;

import java.io.BufferedReader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Response {
	private EnumMap<Field, StringBuilder> content;
	public Response(String status) {
		content = new EnumMap<>(Field.class);
		content.put(Field.status, new StringBuilder(status));
	}
	public Response() {
		content = new EnumMap<>(Field.class);
		content.put(Field.status, new StringBuilder("OK"));
	}
	public Response(HashMap<String, StringBuilder> responseContent) throws RequestException {
		content = new EnumMap<>(Field.class);
		for(Map.Entry<String, StringBuilder> entry: responseContent.entrySet()) {
			content.put(Field.valueOf(entry.getKey()), entry.getValue());
		}
		if (!content.containsKey(Field.status))
			throw RequestException.RESPONSE_ERROR_MISSING_STATUS();
	}
	static public Response parse(BufferedReader in) throws Exception {
		HashMap<String, StringBuilder> content = new HashMap<>();
		String line;
		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (!line.isEmpty()) {
				if (line.equals("end")) break;
				String[] pieces = line.split("\t", 2);
				if (pieces.length != 2)
					content.put(pieces[0].trim(), null);
				else
					content.put(pieces[0].trim(), new StringBuilder(pieces[1].trim()));
			}
		}
		return new Response(content);
	}
	public boolean has(Field field) {
		return content.containsKey(field);
	}
	public StringBuilder get(Field field) {
		return content.get(field);
	}
	public String status() {
		return content.get(Field.status).toString();
	}
	public boolean good() {
		return status().equals("OK");
	}
	public boolean bad() {
		return !good();
	}
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Map.Entry<Field, StringBuilder> entry : content.entrySet()) {
			StringBuilder value = entry.getValue();
			if(value == null) value = new StringBuilder();
			s.append(entry.getKey()).append('\t').append(value).append("\r\n");
		}
		s.append("end\r\n");
		return s.toString();
	}
}
