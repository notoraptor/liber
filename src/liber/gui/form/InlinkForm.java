package liber.gui.form;

import liber.data.InMessage;

public class InlinkForm extends Form {
	private InMessage inlink;
	public InlinkForm(InMessage theInlink) {
		super("Demande reçue");
		assert theInlink != null;
		inlink = theInlink;
	}
	public InMessage inlink() {
		return inlink;
	}
	@Override
	protected String name() {
		return "inlink";
	}
}
