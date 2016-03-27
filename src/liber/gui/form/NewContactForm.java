package liber.gui.form;

import javafx.fxml.FXMLLoader;

public class NewContactForm extends Form {
	public NewContactForm() {
		super("Ajouter un nouveau contact");
	}
	@Override
	protected FormName name() {
		return FormName.newContact;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		return false;
	}
}
