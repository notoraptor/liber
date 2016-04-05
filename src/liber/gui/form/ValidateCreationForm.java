package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.enumeration.AccountProcess;
import liber.gui.control.ValidateCreationController;

public class ValidateCreationForm extends Form {
	private AccountProcess accountProcess;
	private ValidateCreationForm(AccountProcess accountProcess) {
		super("Valider la création du compte");
		this.accountProcess = accountProcess;
	}
	static public ValidateCreationForm fromCreation() {
		return new ValidateCreationForm(AccountProcess.NOT_CONNECTING);
	}
	static public ValidateCreationForm fromConnexion() {
		return new ValidateCreationForm(AccountProcess.CONNECTING);
	}
	public boolean isFromConnexion() {
		return accountProcess == AccountProcess.CONNECTING;
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
