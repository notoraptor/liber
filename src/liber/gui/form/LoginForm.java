package liber.gui.form;

import javafx.fxml.FXMLLoader;

public class LoginForm extends Form {
	public LoginForm() {
		super("Login");
	}
	@Override
	protected FormName name() {
		return FormName.login;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		return false;
	}
}
