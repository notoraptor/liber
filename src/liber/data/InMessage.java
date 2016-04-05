package liber.data;

import liber.enumeration.Encoding;

public class InMessage extends Message {
	private long microtime;
	private boolean acknowledgeLater;
	public InMessage(Contact sender, long microtime, String message) {
		super(sender, message, Encoding.ENCODED);
		this.microtime = microtime;
	}
	public Contact sender() {
		return protagonist();
	}
	public void setAcknowledgeLater(boolean acknowledge) {
		acknowledgeLater = acknowledge;
	}
	public boolean toBeAcknowledged() {
		return acknowledgeLater;
	}
	@Override
	public long microtime() {
		return microtime;
	}
	@Override
	public MessageID id() {
		return new MessageID(this);
	}
}
