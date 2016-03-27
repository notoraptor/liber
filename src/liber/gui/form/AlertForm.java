package liber.gui.form;

import javafx.fxml.FXMLLoader;

public class AlertForm extends Form {
	public AlertForm() {
		super(null);
	}
	@Override
	protected FormName name() {
		return FormName.alert;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		return false;
	}
}
