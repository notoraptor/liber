package liber.gui.form;

import liber.data.InMessage;

public class InlinkForm extends Form {
	private InMessage inlink;
	public InlinkForm(InMessage theInlink) {
		super("Demande re�ue");
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
