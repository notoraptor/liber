package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.data.OutMessage;
import liber.gui.control.OutMessageController;

public class OutMessageForm extends Form {
	private OutMessage message;
	public OutMessageForm(OutMessage theMessage) {
		super(null);
		message = theMessage;
	}
	public OutMessage message() {
		return message;
	}
	@Override
	protected FormName name() {
		return FormName.outMessage;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		OutMessageController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
