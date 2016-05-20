package liber.gui.control;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import liber.command.NewMessageCommand;
import liber.data.*;
import liber.enumeration.CommandField;
import liber.gui.GUI;
import liber.gui.form.*;
import liber.notification.Info;
import liber.notification.Informer;
import liber.notification.Notification;
import liber.notification.info.*;

import java.io.ByteArrayInputStream;

public class DiscussionController {
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
				if(mr.get().liberaddress().equals(contact.liberaddress()))
					addInMessage(mr.get());
			}
			else if(info instanceof MessageCreated) {
				MessageCreated mc = (MessageCreated)info;
				if(mc.get().liberaddress().equals(contact.liberaddress()))
					addOutMessage(mc.get());
			}
			else if(info instanceof OutMessageUpdated) {
				OutMessageUpdated omu = (OutMessageUpdated) info;
				if(omu.get().liberaddress().equals(contact.liberaddress())) {
					Node root = history.lookup("#" + omu.get().id());
					if (root != null) {
						Label messageState = (Label) root.lookup("#messageState");
						if (messageState == null) System.err.println("<<<< erreur >>>>");
						else {
							OutMessage m = omu.get();
							OutMessageController.setState(messageState, m);
						}
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
	private BorderPane messageForm;
	@FXML
	private TextArea message;
	@FXML
	private VBox history;

	@FXML
	void cancel(ActionEvent event) throws Exception {
		//GUI.current.back();
		Form form = GUI.current.getBack();
		if(form instanceof WorkForm) {
			((WorkForm)form).setTabIndex(contact.isIgnored() ? WorkForm.IGNORED_CONTACTS : WorkForm.CONTACTS);
		}
		GUI.current.load(form);
	}
	@FXML
	void send(ActionEvent event) {
		if(contact.isIgnored()) {
			Notification.bad("Vous ne pouvez pas envoyer de message à un contact que vous avez bloqué.");
			return;
		}
		String content = message.getText();
		if(content != null) {
			content = content.trim();
			if(!content.isEmpty()) {
				NewMessageCommand command = new NewMessageCommand();
				command.put(CommandField.contact, liberaddress.getText().trim());
				command.put(CommandField.message, content);
				if(command.run()) {
					message.setText(null);
					// TODO Beaucoup de travail à faire ici.
					// TODO Pas sûr que command.run() == true si le message reste en attente.
				}
			}
		}
	}
	@FXML
	void showContactProfile(ActionEvent event) throws Exception {
		GUI.current.load(new ContactProfileForm(contact));
	}

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
	private void addChildrenCorrectly(Parent root, MessageID nextID) {
		ObservableList<Node> ol = history.getChildren();
		if(ol.isEmpty())
			ol.add(root);
		else {
			int olSize = ol.size();
			int position;
			for (position = olSize - 1; position >= 0; --position) {
				Object userData = ol.get(position).getUserData();
				if (userData != null && userData instanceof MessageID) {
					MessageID previousID = (MessageID) userData;
					if (previousID.compareTo(nextID) <= 0)
						break;
				}
			}
			ol.add(position + 1, root);
		}
	}
	private void addInMessage(InMessage im) throws Exception {
		Parent root = new InMessageForm(im).root();
		MessageID id = im.id();
		root.setUserData(id);
		addChildrenCorrectly(root, id);
		// history.getChildren().add(root);
	}
	private void addOutMessage(OutMessage om) throws Exception {
		Parent root = new OutMessageForm(om).root();
		MessageID id = om.id();
		root.setUserData(id);
		root.setId(id.toString());
		addChildrenCorrectly(root, id);
		//history.getChildren().add(root);
	}
	public void init(DiscussionForm form) throws Exception {
		contact = form.contact();
		GUI.current.setInformer(new DiscussionInformer());
		GUI.current.setCurrentContact(contact);
		history.getChildren().addListener((ListChangeListener<? super Node>) (changed) -> messageListModified = true);
		scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
			if (messageListModified) {
				messageListModified = false;
				Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));
			}
		});
		liberaddress.setText(contact.liberaddress().toString());
		updateContact();
		for(Message message: contact.messages()) {
			if(message instanceof InMessage) {
				addInMessage((InMessage)message);
				//Parent root = new InMessageForm((InMessage)message).root();
				//history.getChildren().add(root);
			} else {
				addOutMessage((OutMessage)message);
			}
		}
		if(contact.isIgnored())
			messageForm.setDisable(true);
	}
}
