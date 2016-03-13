package liber.gui.form;

import liber.Libersaurus;

public class HomeForm extends Form {
	private Libersaurus context;
	public HomeForm() {
		super("Home");
	}
	public void setContext(Libersaurus theContext) {
		context = theContext;
	}
	public Libersaurus getContext() {
		return context;
	}
}
