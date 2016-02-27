package liber.gui.form;

import liber.data.OutMessage;
import liber.gui.control.Controller;

public class OutMessageForm extends Form {
	private OutMessage message;
	public OutMessageForm(OutMessage theMessage) {
		super(null);
		message = theMessage;
	}
	@Override
	public void init(Controller controller) throws Exception {
		controller.load(message);
	}
}
