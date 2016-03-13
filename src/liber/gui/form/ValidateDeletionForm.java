package liber.gui.form;

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
}
