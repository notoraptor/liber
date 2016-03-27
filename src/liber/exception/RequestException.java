package liber.exception;

import liber.enumeration.ErrorCode;
import liber.enumeration.Field;

public class RequestException extends Exception {
	static public RequestException ERROR_OUTLINK_UNKNOWN() {
		return new RequestException(ErrorCode.ERROR_OUTLINK_UNKNOWN);
	}
	static public RequestException ERROR_SECRET() {
		return new RequestException(ErrorCode.ERROR_SECRET);
	}
	static public RequestException ERROR_READING_STREAM() {
		return new RequestException(ErrorCode.ERROR_READING_STREAM);
	}
	static public RequestException REQUEST_ERROR_NO_REQUEST() {
		return new RequestException(ErrorCode.REQUEST_ERROR_NO_REQUEST);
	}
	static public RequestException REQUEST_ERROR_NAME() {
		return new RequestException(ErrorCode.REQUEST_ERROR_NAME);
	}
	static public RequestException REQUEST_ERROR_RECIPIENT() {
		return new RequestException(ErrorCode.REQUEST_ERROR_RECIPIENT);
	}
	static public RequestException REQUEST_ERROR_SENDER() {
		return new RequestException(ErrorCode.REQUEST_ERROR_SENDER);
	}
	static public RequestException REQUEST_ERROR_NAME_MISSING() {
		return new RequestException(ErrorCode.REQUEST_ERROR_NAME_MISSING);
	}
	static public RequestException REQUEST_ERROR_SENDER_MISSING() {
		return new RequestException(ErrorCode.REQUEST_ERROR_SENDER_MISSING);
	}
	static public RequestException REQUEST_ERROR_RECIPIENT_MISSING() {
		return new RequestException(ErrorCode.REQUEST_ERROR_RECIPIENT_MISSING);
	}
	static public RequestException REQUEST_ERROR_SECRET_MISSING() {
		return new RequestException(ErrorCode.REQUEST_ERROR_SECRET_MISSING);
	}
	static public RequestException RESPONSE_ERROR_MISSING_STATUS() {
		return new RequestException(ErrorCode.RESPONSE_ERROR_MISSING_STATUS);
	}
	static public RequestException REQUEST_ERROR_UNKNOWN_REQUEST() {
		return new RequestException(ErrorCode.REQUEST_ERROR_UNKNOWN_REQUEST);
	}
	public static RequestException ERROR_INLINK() {
		return new RequestException(ErrorCode.ERROR_INLINK);
	}
	public static RequestException ERROR_USER_ADDRESS() {
		return new RequestException(ErrorCode.ERROR_USER_ADDRESS);
	}
	public static RequestException ERROR_CONTACT() {
		return new RequestException(ErrorCode.ERROR_CONTACT);
	}
	public static RequestException ERROR_MICROTIME_FORMAT() {
		return new RequestException(ErrorCode.ERROR_MICROTIME_FORMAT);
	}
	public static RequestException ERROR_NO_MESSAGE() {
		return new RequestException(ErrorCode.ERROR_NO_MESSAGE);
	}
	public static RequestException ERROR_HASH() {
		return new RequestException(ErrorCode.ERROR_HASH);
	}
	public static RequestException REQUEST_ERROR_SENDER_IS_RECIPIENT() {
		return new RequestException(ErrorCode.REQUEST_ERROR_SENDER_IS_RECIPIENT);
	}
	private ErrorCode errorCode;
	public RequestException(ErrorCode error_code) {
		this(error_code, null);
	}
	public RequestException(ErrorCode error_code, String info) {
		super(error_code.toString() + (info == null ? "" : '(' + info + ')'));
		errorCode = error_code;
	}
	static public RequestException REQUEST_ERROR_FIELD_MISSING(Field field) {
		return new RequestException(ErrorCode.REQUEST_ERROR_FIELD_MISSING, field.toString());
	}
	static public RequestException RESPONSE_ERROR_GOOD_FIELD_MISSING(Field field) {
		return new RequestException(ErrorCode.RESPONSE_ERROR_GOOD_FIELD_MISSING, field.toString());
	}
	static public RequestException RESPONSE_ERROR_BAD_FIELD_MISSING(Field field) {
		return new RequestException(ErrorCode.RESPONSE_ERROR_BAD_FIELD_MISSING, field.toString());
	}
	public ErrorCode errorCode() {
		return errorCode;
	}
}
