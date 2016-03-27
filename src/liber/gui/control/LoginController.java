package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import liber.Libersaurus;
import liber.card.LibercardReport;
import liber.command.LoginCommand;
import liber.enumeration.CommandField;
import liber.gui.GUI;
import liber.gui.form.ReportForm;
import liber.gui.form.ValidateCreationForm;
import liber.gui.form.ValidateDeletionForm;
import liber.gui.form.WorkForm;

public class LoginController {

	@FXML
	private TextField liberaddressField;

	@FXML
	private PasswordField passwordField;

	@FXML
	void cancel(ActionEvent event) throws Exception {
		GUI.current.back();
	}

	@FXML
	void login(ActionEvent event) throws Exception {
		LoginCommand command = new LoginCommand();
		command.put(CommandField.liberaddress, liberaddressField.getText().trim());
		command.put(CommandField.password, passwordField.getText().trim());
		passwordField.clear();
		if(command.run()) {
			if(Libersaurus.current.account().toConfirm()) {
				GUI.current.load(new ValidateCreationForm(true));
			} else if(Libersaurus.current.account().toDelete()) {
				GUI.current.load(new ValidateDeletionForm(true));
			} else {
				LibercardReport report = Libersaurus.current.reportLibercard();
				System.out.println("Report? " + report.isEmpty());
				if(report.isEmpty())
					GUI.current.load(new WorkForm());
				else
					GUI.current.load(new ReportForm(report));
			}
		}
	}

}
