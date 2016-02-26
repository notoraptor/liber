package liber.data;

import liber.enumeration.MessageState;
import liber.notification.Notification;
import liber.notification.info.OutMessageUpdated;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class OutMessage extends Message {
	private long microtime;
	private MessageState state;
	public OutMessage(Contact recipient, String message) {
		super(recipient, message, false);
		this.microtime = System.currentTimeMillis() * 1000L;
		this.state = MessageState.LOCATION_WAITING;
	}
	public OutMessage(Contact recipient, long microtime, String message) {
		super(recipient, message, true);
		this.microtime = microtime;
		this.state = MessageState.LOCATION_WAITING;
	}
	@Override
	public long microtime() {
		return microtime;
	}
	@Override
	public MessageID id() {
		return new MessageID(this);
	}
	public Contact recipient() {
		return protagonist();
	}
	public MessageState state() {
		return state;
	}
	public boolean isLocationWaiting() {
		return state == MessageState.LOCATION_WAITING;
	}
	public boolean isLiberserverWaiting() {
		return state == MessageState.LIBERSERVER_WAITING;
	}
	public boolean isConfirmationWaiting() {
		return state == MessageState.CONFIRMATION_WAITING;
	}
	public boolean isNotSent() {
		return state == MessageState.NOT_SENT;
	}
	public boolean isSent() {
		return state == MessageState.SENT;
	}
	public void setLiberserverWaiting() {
		setState(MessageState.LIBERSERVER_WAITING);
	}
	public void setLocationWaiting() {
		setState(MessageState.LOCATION_WAITING);
	}
	public void setConfirmationWaiting() {
		setState(MessageState.CONFIRMATION_WAITING);
	}
	public void setNotSent() {
		setState(MessageState.NOT_SENT);
	}
	public void setSent() {
		setState(MessageState.SENT);
	}
	private void setState(MessageState newState) {
		if(state != newState) {
			state = newState;
			protagonist().updateOutMessage(this);
			Notification.info(new OutMessageUpdated(this));
		}
	}
}
