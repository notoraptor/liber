package liber.exception;

import liber.enumeration.ErrorCode;
import liber.enumeration.Field;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class RequestException extends Exception {
	static final public RequestException ERROR_OUTLINK_UNKNOWN = new RequestException(ErrorCode.ERROR_OUTLINK_UNKNOWN);
	static final public RequestException ERROR_SECRET = new RequestException(ErrorCode.ERROR_SECRET);
	static final public RequestException ERROR_INVITATION = new RequestException(ErrorCode.ERROR_INVITATION);
	static final public RequestException ERROR_LINK_IMPOSSIBLE = new RequestException(ErrorCode.ERROR_LINK_IMPOSSIBLE);
	static final public RequestException ERROR_READING_STREAM = new RequestException(ErrorCode.ERROR_READING_STREAM);
	static final public RequestException REQUEST_FORMAT_ERROR = new RequestException(ErrorCode.REQUEST_FORMAT_ERROR);
	static final public RequestException REQUEST_ERROR_NO_REQUEST = new RequestException(ErrorCode.REQUEST_ERROR_NO_REQUEST);
	static final public RequestException REQUEST_ERROR_NAME = new RequestException(ErrorCode.REQUEST_ERROR_NAME);
	static final public RequestException REQUEST_ERROR_RECIPIENT = new RequestException(ErrorCode.REQUEST_ERROR_RECIPIENT);
	static final public RequestException REQUEST_ERROR_SENDER = new RequestException(ErrorCode.REQUEST_ERROR_SENDER);
	static final public RequestException REQUEST_ERROR_NAME_MISSING = new RequestException(ErrorCode.REQUEST_ERROR_NAME_MISSING);
	static final public RequestException REQUEST_ERROR_SENDER_MISSING = new RequestException(ErrorCode.REQUEST_ERROR_SENDER_MISSING);
	static final public RequestException REQUEST_ERROR_RECIPIENT_MISSING = new RequestException(ErrorCode.REQUEST_ERROR_RECIPIENT_MISSING);
	static final public RequestException REQUEST_ERROR_SECRET_MISSING = new RequestException(ErrorCode.REQUEST_ERROR_SECRET_MISSING);
	static final public RequestException RESPONSE_FORMAT_ERROR = new RequestException(ErrorCode.RESPONSE_FORMAT_ERROR);
	static final public RequestException RESPONSE_ERROR_MISSING_STATUS = new RequestException(ErrorCode.RESPONSE_ERROR_MISSING_STATUS);
	static final public RequestException REQUEST_ERROR_UNKNOWN_REQUEST = new RequestException(ErrorCode.REQUEST_ERROR_UNKNOWN_REQUEST);
	public static final RequestException ERROR_INLINK = new RequestException(ErrorCode.ERROR_INLINK);
	public static final RequestException ERROR_USER_ADDRESS = new RequestException(ErrorCode.ERROR_USER_ADDRESS);
	public static final RequestException ERROR_CONTACT = new RequestException(ErrorCode.ERROR_CONTACT);
	public static final RequestException ERROR_MICROTIME_FORMAT = new RequestException(ErrorCode.ERROR_MICROTIME_FORMAT);
	public static final RequestException ERROR_NO_MESSAGE = new RequestException(ErrorCode.ERROR_NO_MESSAGE);
	public static final RequestException ERROR_HASH = new RequestException(ErrorCode.ERROR_HASH);
	public static final RequestException REQUEST_ERROR_SENDER_IS_RECIPIENT = new RequestException(ErrorCode.REQUEST_ERROR_SENDER_IS_RECIPIENT);
	private ErrorCode errorCode;
	public RequestException(ErrorCode error_code) {
		this(error_code, null);
	}
	public RequestException(ErrorCode error_code, String info) {
		super(error_code.toString() + (info == null ? "" : "(" + info + ")"));
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
	static public RequestException RESPONSE_STATUS(String status) {
		return new RequestException(ErrorCode.RESPONSE_STATUS, status);
	}
	public ErrorCode errorCode() {
		return errorCode;
	}
}
