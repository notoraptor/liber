package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import liber.command.NewContactCommand;
import liber.enumeration.CommandField;
import liber.gui.GUI;
import liber.gui.form.Form;
import liber.gui.form.WorkForm;

public class NewContactController {

	@FXML
	private TextField liberaddressField;

	@FXML
	private TextArea invitationField;

	@FXML
	void cancel(ActionEvent event) throws Exception {
		GUI.current.back();
	}

	@FXML
	void newContact(ActionEvent event) throws Exception {
		NewContactCommand command = new NewContactCommand();
		String user = liberaddressField.getText();
		String invitation = invitationField.getText();
		if(user != null) user = user.trim();
		if(invitation != null) invitation = invitation.trim();
		command.put(CommandField.user, user);
		command.put(CommandField.invitation, invitation);
		if(command.run()) {
			Form form = GUI.current.getBack();
			if(form instanceof WorkForm) {
				((WorkForm)form).setTabIndex(WorkForm.OUTLINKS);
				GUI.current.load(form);
			} else {
				GUI.current.load(new WorkForm(WorkForm.OUTLINKS));
			}
		}
	}

}
