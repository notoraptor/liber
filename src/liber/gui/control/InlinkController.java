package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import liber.command.AcceptInlinkCommand;
import liber.command.RefuseInlinkCommand;
import liber.enumeration.CommandField;
import liber.gui.GUI;
import liber.gui.Question;
import liber.gui.form.Form;
import liber.gui.form.WorkForm;
import liber.notification.Info;
import liber.notification.Informer;
import liber.notification.info.InlinkDeleted;

public class InlinkController {
	class InlinkInformer implements Informer {
		@Override
		public void inform(Info info) throws Exception {
			if(info instanceof InlinkDeleted) {
				InlinkDeleted id = (InlinkDeleted) info;
				if(id.get().sender().liberaddress().toString().equals(liberaddress.getText())) {
					GUI.current.back();
				}
			}
		}
	}
	@FXML
	protected void initialize() {
		GUI.current.notifier().setInformer(new InlinkInformer());
	}

	@FXML
	private Label invitation;

	@FXML
	private Label username;

	@FXML
	private Label liberaddress;

	@FXML
	void acceptInlink(ActionEvent event) throws Exception {
		Question question = new Question();
		question.setTitle("Accepter la demande");
		question.setQuestion("Accepter la demande de\n" + liberaddress.getText() + " ?");
		question.setPositiveLabel("Accepter la demande");
		question.setAction(() -> {
			AcceptInlinkCommand command = new AcceptInlinkCommand();
			command.put(CommandField.userLiberaddress, liberaddress.getText());
			if(command.run()) {
				Form form = GUI.current.getBack();
				if(form instanceof WorkForm) {
					((WorkForm)form).setTabIndex(WorkForm.CONTACTS);
					GUI.current.load(form);
				} else {
					GUI.current.load(new WorkForm(WorkForm.CONTACTS));
				}
			}
		});
		question.show();
	}

	@FXML
	void cancel(ActionEvent event) throws Exception {
		Form form = GUI.current.getBack();
		if(form instanceof WorkForm) {
			((WorkForm)form).setTabIndex(WorkForm.INLINKS);
			GUI.current.load(form);
		} else {
			GUI.current.load(form);
		}
	}

	@FXML
	void refuseInlink(ActionEvent event) throws Exception {
		Question question = new Question();
		question.setTitle("Rejeter la demande.");
		question.setQuestion("Voulez-vous vraiment rejeter la demande de\n" + liberaddress.getText() + " ?");
		question.setPositiveLabel("Rejeter la demande");
		question.setAction(() -> {
			RefuseInlinkCommand command = new RefuseInlinkCommand();
			command.put(CommandField.userLiberaddress, liberaddress.getText());
			if(command.run()) {
				Form form = GUI.current.getBack();
				if(form instanceof WorkForm) {
					((WorkForm)form).setTabIndex(WorkForm.INLINKS);
					GUI.current.load(form);
				} else {
					GUI.current.load(new WorkForm(WorkForm.INLINKS));
				}
			}
		});
		question.show();
	}

}
