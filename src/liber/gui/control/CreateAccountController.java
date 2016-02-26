package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import liber.command.CreateAccountCommand;
import liber.enumeration.CommandField;
import liber.gui.GUI;
import liber.gui.form.ValidateCreationForm;

public class CreateAccountController {

	@FXML
	private TextField liberserver;

	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	@FXML
	private PasswordField passwordAgain;

	@FXML
	void cancel(ActionEvent event) throws Exception {
		GUI.current.back();
	}

	@FXML
	void createAccount(ActionEvent event) throws Exception {
		CreateAccountCommand command = new CreateAccountCommand();
		command.put(CommandField.liberserver, liberserver.getText().trim());
		command.put(CommandField.username, username.getText().trim());
		command.put(CommandField.password, password.getText().trim());
		command.put(CommandField.passwordAgain, passwordAgain.getText().trim());
		password.clear();
		passwordAgain.clear();
		if(command.run())
			GUI.current.load(new ValidateCreationForm());
	}

}
