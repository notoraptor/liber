package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.data.Contact;
import liber.gui.control.ContactProfileController;

public class ContactProfileForm extends Form {
	private Contact contact;
	public ContactProfileForm(Contact contact) {
		super("Profil de " + contact.appellation());
		this.contact = contact;
	}
	public Contact contact() {
		return contact;
	}
	@Override
	protected FormName name() {
		return FormName.contactProfile;
	}
	@Override
	protected boolean control(FXMLLoader loader) throws Exception {
		ContactProfileController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
