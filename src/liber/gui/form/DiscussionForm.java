package liber.gui.form;

import liber.data.Contact;

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
	protected String name() {
		return "discussion";
	}
}
