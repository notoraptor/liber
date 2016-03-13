package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import liber.command.CancelOutlinkCommand;
import liber.data.OutMessage;
import liber.enumeration.CommandField;
import liber.gui.GUI;
import liber.gui.Question;
import liber.gui.form.Form;
import liber.gui.form.OutlinkForm;
import liber.gui.form.WorkForm;
import liber.notification.Info;
import liber.notification.Informer;
import liber.notification.info.OutlinkAccepted;
import liber.notification.info.OutlinkDeleted;

public class OutlinkController {
	class OutlinkInformer implements Informer {
		@Override
		public void inform(Info info) throws Exception {
			if(info instanceof OutlinkDeleted) {
				OutlinkDeleted od = (OutlinkDeleted) info;
				if(od.get().recipient().liberaddress().toString().equals(liberaddress.getText())) {
					GUI.current.back();
				}
				return;
			}
			if(info instanceof OutlinkAccepted) {
				OutlinkAccepted oa = (OutlinkAccepted) info;
				if(oa.get().recipient().liberaddress().toString().equals(liberaddress.getText())) {
					WorkForm form = GUI.current.findWorkForm();
					form.setTabIndex(WorkForm.CONTACTS);
					GUI.current.load(form);
				}
			}
		}
	}

	@FXML
	protected void init(OutlinkForm form) {
		GUI.current.notifier().setInformer(new OutlinkInformer());
		OutMessage outlink = form.outlink();
		invitation.setText(outlink.decodedContent());
		username.setText(outlink.recipient().username());
		liberaddress.setText(outlink.recipient().liberaddress().toString());
	}

	@FXML
	private Label invitation;

	@FXML
	private Label username;

	@FXML
	private Label liberaddress;

	@FXML
	void cancel(ActionEvent event) throws Exception {
		Form form = GUI.current.getBack();
		if(form instanceof WorkForm) {
			((WorkForm)form).setTabIndex(WorkForm.OUTLINKS);
			GUI.current.load(form);
		} else {
			GUI.current.load(form);
		}
	}

	@FXML
	void cancelOutlink(ActionEvent event) throws Exception {
		Question question = new Question();
		question.setTitle("Annuler la demande");
		question.setQuestion("Voulez-vous vraimment annuler la demande envoyée à\n" + liberaddress.getText() + " ?");
		question.setPositiveLabel("Annuler la demande");
		question.setAction(() -> {
			CancelOutlinkCommand command = new CancelOutlinkCommand();
			command.put(CommandField.userLiberaddress, liberaddress.getText());
			if(command.run()) {
				Form form = GUI.current.getBack();
				if(form instanceof WorkForm) {
					((WorkForm)form).setTabIndex(WorkForm.OUTLINKS);
					GUI.current.load(form);
				} else {
					GUI.current.load(new WorkForm(WorkForm.OUTLINKS));
				}
			}
		});
		question.show();
	}

}
