package liber.gui.form;

import liber.data.OutMessage;
import liber.gui.control.Controller;

/**
 * Created by HPPC on 24/02/2016.
 */
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
