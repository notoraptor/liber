package liber.data;

public class MessageID implements Comparable<MessageID> {
	static private final int IN = 1;
	static private final int OUT = 2;
	private int type;
	private long time;
	private long randomID;
	public MessageID(InMessage message) {
		time = message.microtime();
		type = IN;
	}
	public MessageID(OutMessage message) {
		time = message.microtime();
		type = OUT;
	}
	private MessageID(int type, long time) {
		this.type = type;
		this.time = time;
	}
	static public MessageID forIn(long time) {
		return new MessageID(IN, time);
	}
	static public MessageID forOut(long time) {
		return new MessageID(OUT, time);
	}
	@Override
	public int compareTo(MessageID other) {
		long t = time - other.time;
		if (t == 0) t = type - other.type;
		return t == 0 ? 0 : (t < 0 ? -1 : +1);
	}
	@Override
	public String toString() {
		return type + "-" + time;
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof MessageID) {
			MessageID otherID = (MessageID) other;
			return time == otherID.time && type == otherID.type;
		}
		return false;
	}
}
