package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.gui.control.ValidateCreationController;

public class ValidateCreationForm extends Form {
	private boolean fromConnexion;
	public ValidateCreationForm() {
		super("Valider la création du compte");
	}
	public ValidateCreationForm(boolean fromConnexion) {
		this();
		this.fromConnexion = fromConnexion;
	}
	public boolean isFromConnexion() {
		return fromConnexion;
	}
	@Override
	protected FormName name() {
		return FormName.validateCreation;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		ValidateCreationController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
