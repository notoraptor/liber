package liber.gui.form;

import liber.data.InMessage;
import liber.gui.control.Controller;

/**
 * Created by HPPC on 24/02/2016.
 */
public class InMessageForm extends Form {
	private InMessage message;
	public InMessageForm(InMessage theMessage) {
		super(null);
		message = theMessage;
	}
	@Override
	public void init(Controller controller) throws Exception {
		controller.load(message);
	}
}
