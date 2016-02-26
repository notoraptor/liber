package liber.gui.control;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import liber.data.InMessage;

public class InMessageController implements Controller {

	@FXML
	private Label timestamp;

	@FXML
	private Label appellation;

	@FXML
	private Label content;

	@Override
	public void load(Object object) {
		if(object instanceof InMessage) {
			InMessage message = (InMessage) object;
			appellation.setText(message.sender().appellation());
			content.setText(message.decodedContent());
			timestamp.setText(message.timeString());
		}
	}
}
