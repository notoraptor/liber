package liber.gui.form;

import liber.data.Contact;
import liber.gui.control.Controller;

public class DiscussionForm extends Form {
	private Contact contact;
	public DiscussionForm(Contact theContact) {
		super("Discussion avec " + theContact.appellation() + ".");
		contact = theContact;
	}
	@Override public void init(Controller controller) throws Exception {
		controller.load(contact);
	}
}
