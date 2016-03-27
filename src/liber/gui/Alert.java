package liber.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import liber.gui.form.AlertForm;

public class Alert {
	public Alert(String title, String message, String buttonTitle) throws Exception {
		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Parent root = new AlertForm().root();
		Label titleLabel = (Label)root.lookup("#title");
		Label messageLabel = (Label)root.lookup("#message");
		Button okButton = (Button)root.lookup("#button");
		titleLabel.setText(title);
		messageLabel.setText(message);
		okButton.setText(buttonTitle);
		okButton.setOnAction((event) -> dialogStage.close());
		dialogStage.setScene(new Scene(root));
		dialogStage.setTitle(title);
		dialogStage.show();
	}
	public Alert(String title, String message) throws Exception {
		this(title, message, "OK");
	}
}