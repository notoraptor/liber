package liber.gui.form;

import liber.data.Contact;
import liber.gui.control.Controller;
import liber.gui.form.Form;

public class ContactProfileForm extends Form {
	private Contact contact;
	public ContactProfileForm(Contact contact) {
		super("Profil de " + contact.appellation());
		this.contact = contact;
	}
	@Override public void init(Controller controller) throws Exception {
		controller.load(contact);
	}
}
