package liber.gui.control;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import liber.data.OutMessage;

import java.util.Date;

public class OutMessageController implements Controller {
	static public final String notSent = "\u274c";
	static public final String locationWaiting = "- - -";
	static public final String liberserverWaiting = "\u2708";
	static public final String confirmationWaiting = "\u2753";
	static public final String sent = "\u2713\u2713";
	static public void setState(Label messageState, OutMessage m) {
		if(m.isNotSent()) {
			messageState.setText(OutMessageController.notSent);
			messageState.setTextFill(Color.RED);
		} else if(m.isLocationWaiting()) {
			messageState.setText(OutMessageController.locationWaiting);
			messageState.setTextFill(Color.RED);
		} else if(m.isLiberserverWaiting()) {
			messageState.setText(OutMessageController.liberserverWaiting);
			messageState.setTextFill(Color.RED);
		} else if(m.isConfirmationWaiting()) {
			messageState.setText(OutMessageController.confirmationWaiting);
			messageState.setTextFill(Color.RED);
		} else if(m.isSent()) {
			messageState.setText(OutMessageController.sent);
			messageState.setTextFill(Color.GREEN);
		}
	}
	@FXML
	private Label timestamp;

	@FXML
	private Label content;

	@FXML
	private Label messageState;

	@Override
	public void load(Object object) {
		if(object instanceof OutMessage) {
			OutMessage message = (OutMessage) object;
			content.setText(message.decodedContent());
			timestamp.setText(message.timeString());
			setState(messageState, message);
		}
	}
}
