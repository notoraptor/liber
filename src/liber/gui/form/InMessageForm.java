package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.data.InMessage;
import liber.gui.control.InMessageController;

public class InMessageForm extends Form {
	private InMessage message;
	public InMessageForm(InMessage theMessage) {
		super(null);
		message = theMessage;
	}
	public InMessage message() {
		return message;
	}
	@Override
	protected FormName name() {
		return FormName.inMessage;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		InMessageController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
