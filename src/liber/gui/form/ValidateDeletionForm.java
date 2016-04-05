package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.enumeration.AccountProcess;
import liber.gui.control.ValidateDeletionController;

public class ValidateDeletionForm extends Form {
	private AccountProcess accountProcess;
	private ValidateDeletionForm(AccountProcess accountProcess) {
		super("Valider la suppression du compte");
		this.accountProcess = accountProcess;
	}
	static public ValidateDeletionForm fromDeletion() {
		return new ValidateDeletionForm(AccountProcess.NOT_CONNECTING);
	}
	static public ValidateDeletionForm fromConnexion() {
		return new ValidateDeletionForm(AccountProcess.CONNECTING);
	}
	public boolean isFromConnexion() {
		return accountProcess == AccountProcess.CONNECTING;
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
