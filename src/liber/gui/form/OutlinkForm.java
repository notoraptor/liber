package liber.gui.form;

import liber.data.OutMessage;

public class OutlinkForm extends Form {
	private OutMessage outlink;
	public OutlinkForm(OutMessage theOutlink) {
		super("Demande reçue");
		assert theOutlink != null;
		outlink = theOutlink;
	}
	public OutMessage outlink() {
		return outlink;
	}
	@Override
	protected String name() {
		return "outlink";
	}
}
