package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.data.Contact;
import liber.gui.control.DiscussionController;

public class DiscussionForm extends Form {
	private Contact contact;
	public DiscussionForm(Contact theContact) {
		super("Discussion avec " + theContact.appellation() + '.');
		contact = theContact;
	}
	public Contact contact() {
		return contact;
	}
	@Override
	protected FormName name() {
		return FormName.discussion;
	}
	@Override
	protected boolean control(FXMLLoader loader) throws Exception {
		DiscussionController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
