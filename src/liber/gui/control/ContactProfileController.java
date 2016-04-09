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
import liber.gui.form.ContactProfileForm;
import liber.gui.form.WorkForm;
import liber.notification.Info;
import liber.notification.Informer;
import liber.notification.Notification;
import liber.notification.info.ContactDeleted;
import liber.notification.info.ContactUpdated;

import java.io.ByteArrayInputStream;

public class ContactProfileController {
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
	void confirmContactDeletion(ActionEvent event) throws Exception {
		Question question = new Question();
		question.setTitle("Supprimer le contact");
		question.setQuestion("Voulez-vous vraiment supprimer le contact \n" + contact.appellation() + "?\n" +
				"Il sera averti de la suppression de votre relation!");
		question.setPositiveLabel("Supprimer le contact");
		question.setPositiveAction(() -> {
			DeleteContactCommand command = new DeleteContactCommand();
			command.put(CommandField.contactLiberaddress, contact.liberaddress().toString());
			if(command.run()) {
				backToWork();
			}
		});
		question.show();
	}
	@FXML
	void confirmIgnoreContact(ActionEvent event) throws Exception {
		Question question = new Question();
		if(contact.isIgnored()) {
			question.setTitle("Débloquer le contact");
			question.setQuestion("Débloquer " + contact.appellation() + " ?");
			question.setPositiveLabel("Débloquer le contact");
		} else {
			question.setTitle("Bloquer le contact");
			question.setQuestion("Bloquer " + contact.appellation() + " ?");
			question.setPositiveLabel("Bloquer le contact");
		}
		question.setPositiveAction(() -> {
			contact.setIgnored(!contact.isIgnored());
			backToWork();
		});
		question.show();
	}
	@FXML
	void deleteHistory(ActionEvent event) throws Exception {
		Question question = new Question();
		question.setTitle("Supprimer l'historique");
		question.setQuestion("Voulez-vous vraiment supprimer l'historique de discussion avec\n" + contact.appellation() + '?');
		question.setPositiveLabel("Supprimer l'historique");
		question.setPositiveAction(() -> {
			contact.clearHistory();
			Notification.good("Historique supprimé pour " + contact.appellation() + '.');
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
			}
			else if(info instanceof ContactDeleted) {
				ContactDeleted cd = (ContactDeleted) info;
				if(cd.get().liberaddress().equals(contact.liberaddress()))
					backToWork();
			}
		}
	}
	private Contact contact;
	private void backToWork() throws Exception {
		WorkForm form = GUI.current.findWorkForm();
		form.setTabIndex(contact.isIgnored() ? WorkForm.IGNORED_CONTACTS : WorkForm.CONTACTS);
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
	public void init(ContactProfileForm form) {
		contact = form.contact();
		GUI.current.notifier().setInformer(new ContactProfileInformer());
		GUI.current.notifier().setCurrentContact(contact);
		title.setText((contact.isIgnored() ? "(\u26d4) " : "") + "Profil de " + contact.username());
		liberaddress.setText(contact.liberaddress().toString());
		updateContact();
	}
}
