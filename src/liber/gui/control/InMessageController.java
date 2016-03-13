package liber.gui.control;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import liber.data.InMessage;
import liber.gui.form.InMessageForm;

public class InMessageController {

	@FXML
	private Label timestamp;

	@FXML
	private Label appellation;

	@FXML
	private Label content;

	public void init(InMessageForm form) {
		InMessage message = form.message();
		appellation.setText(message.sender().appellation());
		content.setText(message.decodedContent());
		timestamp.setText(message.timeString());
	}
}
