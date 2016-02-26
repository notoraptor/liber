package liber.gui.control;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import liber.command.NewMessageCommand;
import liber.data.Contact;
import liber.data.InMessage;
import liber.data.Message;
import liber.data.OutMessage;
import liber.enumeration.CommandField;
import liber.gui.GUI;
import liber.gui.form.ContactProfileForm;
import liber.gui.form.InMessageForm;
import liber.gui.form.OutMessageForm;
import liber.notification.Info;
import liber.notification.Informer;
import liber.notification.info.*;

import java.io.ByteArrayInputStream;

public class DiscussionController implements Controller {
	static public final String onlineString = "\u2714";
	static public final String offlineString = "\u2716";
	class DiscussionInformer implements Informer {
		@Override
		public void inform(Info info) throws Exception {
			if(info instanceof ContactUpdated) {
				ContactUpdated cu = (ContactUpdated)info;
				if(cu.get().liberaddress().equals(contact.liberaddress()))
					updateContact();
			}
			else if(info instanceof ContactDeleted) {
				ContactDeleted cd = (ContactDeleted) info;
				if(cd.get().liberaddress().equals(contact.liberaddress()))
					GUI.current.back();
			}
			else if(info instanceof MessageReceived) {
				MessageReceived mr = (MessageReceived)info;
				Parent root = new InMessageForm(mr.get()).root();
				history.getChildren().add(root);
				// TODO: Comment faire défiler correctement le scrollpane vers le bas quand un message est ajouté.
				// TODO: Le code suivant semble fonctionner pour outmessage (ci-après) mais pas pour inmessage (ici).
			}
			else if(info instanceof MessageCreated) {
				MessageCreated mc = (MessageCreated)info;
				addOutMessage(mc.get());
			}
			else if(info instanceof OutMessageUpdated) {
				OutMessageUpdated omu = (OutMessageUpdated) info;
				Node root = history.lookup("#" + omu.get().id());
				if(root != null) {
					Label messageState = (Label) root.lookup("#messageState");
					if(messageState == null) System.err.println("<<<< erreur >>>>");
					else {
						OutMessage m = omu.get();
						OutMessageController.setState(messageState, m);
					}
				}
			}
		}
	}

	@FXML
	public ScrollPane scrollPane;
	@FXML
	private Label photo;
	@FXML
	private Label appellation;
	@FXML
	private Label liberaddress;
	@FXML
	private Label status;
	@FXML
	private Label online;
	@FXML
	private TextArea message;
	@FXML
	private VBox history;

	private Contact contact;
	private boolean messageListModified;
	private void updateContact() {
		liberaddress.setText(contact.liberaddress().toString());
		appellation.setText(contact.appellation());
		status.setText(contact.info().status());
		if(contact.online()) {
			online.setText(DiscussionController.onlineString);
			online.setTextFill(Color.GREEN);
		} else {
			online.setText(DiscussionController.offlineString);
			online.setTextFill(Color.RED);
		}
		if(contact.info().hasPhoto()) {
			photo.setText(null);
			photo.setGraphic(ProfileController.instanciateImageView(
					new Image(new ByteArrayInputStream(contact.info().photoBytes())), 40
			));
		} else {
			photo.setText(WorkController.noUserPhotoString);
		}
	}
	private void addOutMessage(OutMessage om) throws Exception {
		Parent root = new OutMessageForm(om).root();
		root.setId(om.id().toString());
		history.getChildren().add(root);
	}

	@Override
	public void load(Object resource) throws Exception {
		if(resource instanceof Contact) {
			GUI.current.notifier().setInformer(new DiscussionInformer());
			history.getChildren().addListener((ListChangeListener<? super Node>) (changed) -> {
				messageListModified = true;
			});
			scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
				if (messageListModified) {
					messageListModified = false;
					Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));
				}
			});
			contact = (Contact)resource;
			liberaddress.setText(contact.liberaddress().toString());
			updateContact();
			for(Message message: contact.messages()) {
				if(message instanceof InMessage) {
					Parent root = new InMessageForm((InMessage)message).root();
					history.getChildren().add(root);
				} else {
					addOutMessage((OutMessage)message);
				}
			}
		}
	}
	@FXML
	void cancel(ActionEvent event) throws Exception {
		GUI.current.back();
	}
	@FXML
	void send(ActionEvent event) {
		String content = message.getText();
		if(content != null) {
			content = content.trim();
			if(!content.isEmpty()) {
				NewMessageCommand command = new NewMessageCommand();
				command.put(CommandField.contact, liberaddress.getText().trim());
				command.put(CommandField.message, content);
				if(command.run()) {
					message.setText(null);
					/* Actuellement inutile (un MessageCreated est émis pendant la commande).
					OutMessage message = Libersaurus.current.getContact(liberaddress.getText()).getLastOutMessage();
					Parent root = new OutMessageForm(message).root();
					history.getChildren().add(root);
					*/
					// TODO Beaucoup de travail à faire ici.
					// TODO Pas sûr que command.run == true si le message reste en attente.
				}
			}
		}
	}
	@FXML
	void showContactProfile(ActionEvent event) throws Exception {
		GUI.current.load(new ContactProfileForm(contact));
		// TODO ...
	}

}
