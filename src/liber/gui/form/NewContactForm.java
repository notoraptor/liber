package liber.gui.form;

public class NewContactForm extends Form {
	public NewContactForm() {
		super("Ajouter un nouveau contact");
	}
	@Override
	protected String name() {
		return "newContact";
	}
}
