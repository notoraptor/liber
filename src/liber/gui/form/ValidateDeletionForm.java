package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.gui.control.ValidateDeletionController;

public class ValidateDeletionForm extends Form {
	private boolean fromConnexion;
	public ValidateDeletionForm() {
		super("Valider la suppression du compte");
	}
	public ValidateDeletionForm(boolean fromConnexion) {
		this();
		this.fromConnexion = fromConnexion;
	}
	public boolean isFromConnexion() {
		return fromConnexion;
	}
	@Override
	protected FormName name() {
		return FormName.validateDeletion;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		ValidateDeletionController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
