package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.data.InMessage;
import liber.gui.control.InlinkController;

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
	protected FormName name() {
		return FormName.inlink;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		InlinkController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
