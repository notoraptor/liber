package liber.data;

/**
 * Created by HPPC on 21/02/2016.
 */
public class InMessage extends Message {
	private long microtime;
	private boolean acknowledgeLater;
	public InMessage(Contact sender, long microtime, String message) {
		super(sender, message, true);
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
