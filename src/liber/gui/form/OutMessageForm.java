package liber.gui.form;

import liber.data.OutMessage;

public class OutMessageForm extends Form {
	private OutMessage message;
	public OutMessageForm(OutMessage theMessage) {
		super(null);
		message = theMessage;
	}
	public OutMessage message() {
		return message;
	}
}
