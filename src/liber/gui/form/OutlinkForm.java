package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.data.OutMessage;
import liber.gui.control.OutlinkController;

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
	protected FormName name() {
		return FormName.outlink;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		OutlinkController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
