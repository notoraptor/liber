package liber.gui.form;

import liber.data.InMessage;

public class InMessageForm extends Form {
	private InMessage message;
	public InMessageForm(InMessage theMessage) {
		super(null);
		message = theMessage;
	}
	public InMessage message() {
		return message;
	}
}
