package liber.gui.form;

import javafx.fxml.FXMLLoader;

public class LinkForm extends Form {
	public LinkForm() {
		super(null);
	}
	@Override
	protected FormName name() {
		return FormName.link;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		return false;
	}
}
