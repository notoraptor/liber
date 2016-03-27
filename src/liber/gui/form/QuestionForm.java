package liber.gui.form;

import javafx.fxml.FXMLLoader;

public class QuestionForm extends Form {
	public QuestionForm() {
		super(null);
	}
	@Override
	protected FormName name() {
		return FormName.question;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		return false;
	}
}
