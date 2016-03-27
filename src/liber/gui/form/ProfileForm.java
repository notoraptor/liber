package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.Libersaurus;
import liber.data.Account;
import liber.gui.control.ProfileController;

public class ProfileForm extends Form {
	public ProfileForm() {
		super("Profile of " + Libersaurus.current.account().appellation());
	}
	public Account account() {
		return Libersaurus.current.account();
	}
	@Override
	protected FormName name() {
		return FormName.profile;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		ProfileController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
