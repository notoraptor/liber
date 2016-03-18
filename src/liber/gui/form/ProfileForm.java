package liber.gui.form;

import liber.Libersaurus;
import liber.data.Account;

public class ProfileForm extends Form {
	public ProfileForm() {
		super("Profile of " + Libersaurus.current.account().appellation());
	}
	public Account account() {
		return Libersaurus.current.account();
	}
	@Override
	protected String name() {
		return "profile";
	}
}
