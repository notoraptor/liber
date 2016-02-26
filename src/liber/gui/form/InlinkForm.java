package liber.gui.form;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import liber.data.InMessage;

/**
 * Created by HPPC on 23/02/2016.
 */
public class InlinkForm extends Form {
	private InMessage inlink;
	public InlinkForm(InMessage theInlink) {
		super("Demande reçue");
		assert theInlink != null;
		inlink = theInlink;
	}
	@Override public void init(Parent root) {
		Label invitation = (Label) root.lookup("#invitation");
		Label username = (Label) root.lookup("#username");
		Label liberaddress = (Label) root.lookup("#liberaddress");
		invitation.setText(inlink.decodedContent());
		username.setText(inlink.sender().username());
		liberaddress.setText(inlink.sender().liberaddress().toString());
	}
}
