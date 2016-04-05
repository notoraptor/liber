package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import liber.Features;
import liber.command.ValidateDeletionCommand;
import liber.enumeration.CommandField;
import liber.gui.GUI;
import liber.gui.form.ValidateDeletionForm;

import java.io.ByteArrayInputStream;

public class ValidateDeletionController {

	@FXML
	private Label infoLabel;

	@FXML
	private ImageView imageLabel;

	@FXML
	private TextField captchaLabel;

	@FXML
	void validateDeletion(ActionEvent event) throws Exception {
		ValidateDeletionCommand command = new ValidateDeletionCommand();
		command.put(CommandField.captchaCode, captchaLabel.getText().trim());
		if(command.run()) {
			GUI.current.fullBack();
		} else {
			byte[] data = Features.getCaptchaImageForDeletion();
			if(data != null) {
				imageLabel.setImage(new Image(new ByteArrayInputStream(data)));
			} else imageLabel.setImage(null);
		}
	}

	public void init(ValidateDeletionForm form) {
		if(form.isFromConnexion()) {
			infoLabel.setText("Votre compte est en attente de suppression.");
		}
		byte[] data = Features.getCaptchaImageForDeletion();
		if(data != null) {
			imageLabel.setImage(new Image(new ByteArrayInputStream(data)));
		}
	}
}
