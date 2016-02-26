package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import liber.gui.form.CreateAccountForm;
import liber.gui.GUI;
import liber.gui.form.LoginForm;

public class HomeController {
	@FXML
	void createAccount(ActionEvent event) throws Exception {
		GUI.current.load(new CreateAccountForm());
	}
	@FXML
	void login(ActionEvent event) throws Exception {
		GUI.current.load(new LoginForm());
	}
}
