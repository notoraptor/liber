package liber.gui.form;

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
	protected String name() {
		return "validateCreation";
	}
}
