package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import liber.Libersaurus;
import liber.command.Command;
import liber.command.ValidateCreationCommand;
import liber.enumeration.CommandField;
import liber.gui.GUI;
import liber.gui.form.WorkForm;

import java.io.ByteArrayInputStream;

public class ValidateCreationController {

	@FXML
	private ImageView imageLabel;

	@FXML
	private TextField captchaLabel;

	@FXML
	private Label infoLabel;

	@FXML
	void validateCreation(ActionEvent event) throws Exception {
		Command command = new ValidateCreationCommand();
		command.put(CommandField.captchaCode, captchaLabel.getText().trim());
		if(command.run()) {
			GUI.current.load(new WorkForm());
		} else {
			byte[] data = Libersaurus.current.features().getCaptchaImageForCreation();
			if(data != null) {
				imageLabel.setImage(new Image(new ByteArrayInputStream(data)));
			} else imageLabel.setImage(null);
		}
	}

}
