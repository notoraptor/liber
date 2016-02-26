package liber.gui.form;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import liber.data.OutMessage;

/**
 * Created by HPPC on 23/02/2016.
 */
public class OutlinkForm extends Form {
	private OutMessage outlink;
	public OutlinkForm(OutMessage theOutlink) {
		super("Demande reçue");
		assert theOutlink != null;
		outlink = theOutlink;
	}
	@Override public void init(Parent root) {
		Label invitation = (Label) root.lookup("#invitation");
		Label username = (Label) root.lookup("#username");
		Label liberaddress = (Label) root.lookup("#liberaddress");
		invitation.setText(outlink.decodedContent());
		username.setText(outlink.recipient().username());
		liberaddress.setText(outlink.recipient().liberaddress().toString());
	}
}
