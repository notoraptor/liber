package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import liber.command.DeleteContactCommand;
import liber.data.Contact;
import liber.enumeration.CommandField;
import liber.gui.GUI;
import liber.gui.Question;
import liber.gui.form.WorkForm;
import liber.notification.Info;
import liber.notification.Informer;
import liber.notification.Notification;
import liber.notification.info.ContactDeleted;
import liber.notification.info.ContactUpdated;

import java.io.ByteArrayInputStream;

public class ContactProfileController implements Controller {
	@FXML
	private Label title;
	@FXML
	private Label appellation;
	@FXML
	private Label liberaddress;
	@FXML
	private Button photo;
	@FXML
	private Label status;

	@FXML
	void cancel(ActionEvent event) throws Exception {
		GUI.current.back();
	}
	@FXML
	void confirmContactDeleltion(ActionEvent event) throws Exception {
		Question question = new Question();
		question.setTitle("Supprimer le contact");
		question.setQuestion("Voulez-vous vraiment supprimer le contact \n" + contact.appellation() + "?\n" +
				"Il sera averti de la suppression de votre relation!");
		question.setPositiveLabel("Supprimer le contact");
		question.setAction(() -> {
			DeleteContactCommand command = new DeleteContactCommand();
			command.put(CommandField.contactLiberaddress, contact.liberaddress().toString());
			if(command.run()) {
				backToWork();
			}
		});
		question.show();
	}
	@FXML
	void deleteHistory(ActionEvent event) throws Exception {
		Question question = new Question();
		question.setTitle("Supprimer l'historique");
		question.setQuestion("Voulez-vous vraiment supprimer l'historique de discussion avec\n" + contact.appellation() + "?");
		question.setPositiveLabel("Supprimer l'historique");
		question.setAction(() -> {
			contact.clearHistory();
			Notification.good("Historique supprimé pour " + contact.appellation() + ".");
		});
		question.show();
	}

	class ContactProfileInformer implements Informer {
		@Override
		public void inform(Info info) throws Exception {
			if(info instanceof ContactUpdated) {
				ContactUpdated cu = (ContactUpdated) info;
				if(cu.get().liberaddress().equals(contact.liberaddress()))
					updateContact();
				return;
			}
			if(info instanceof ContactDeleted) {
				ContactDeleted cd = (ContactDeleted) info;
				if(cd.get().liberaddress().equals(contact.liberaddress()))
					backToWork();
				return;
			}
		}
	}
	private Contact contact;

	private void backToWork() throws Exception {
		WorkForm form = GUI.current.findWorkForm();
		form.setTabIndex(WorkForm.CONTACTS);
		GUI.current.load(form);
	}

	private void updateContact() {
		appellation.setText(contact.appellation());
		status.setText(contact.info().status());
		if(contact.info().hasPhoto()) {
			photo.setText(null);
			photo.setGraphic(ProfileController.instanciateImageView(
					new Image(new ByteArrayInputStream(contact.info().photoBytes())), 300
			));
		} else {
			photo.setText(WorkController.noUserPhotoString);
		}
	}

	@Override
	public void load(Object resource) throws Exception {
		if(resource instanceof Contact) {
			GUI.current.notifier().setInformer(new ContactProfileInformer());
			contact = (Contact) resource;
			title.setText("Profil de " + contact.username());
			liberaddress.setText(contact.liberaddress().toString());
			updateContact();
		}
	}
}
