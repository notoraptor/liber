package liber.gui.form;

import javafx.fxml.FXMLLoader;

public class CreateAccountForm extends Form {
	public CreateAccountForm() {
		super("Create Account");
	}
	@Override
	protected FormName name() {
		return FormName.createAccount;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		return false;
	}
}
