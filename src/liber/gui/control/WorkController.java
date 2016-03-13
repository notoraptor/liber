package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import liber.Libersaurus;
import liber.command.DeleteAccountCommand;
import liber.command.LogoutCommand;
import liber.data.Account;
import liber.data.Contact;
import liber.data.InMessage;
import liber.data.OutMessage;
import liber.gui.GUI;
import liber.gui.Question;
import liber.gui.form.*;
import liber.notification.Info;
import liber.notification.Informer;
import liber.notification.info.*;

import java.io.ByteArrayInputStream;

public class WorkController {
	//static public final String onlineSymbol = "\u2605";
	//static public final String offlineSymbol = "\u2606";
	static public final String noUserPhotoString = "\ud83d\ude09";
	class WorkInformer implements Informer {
		@Override
		public void inform(Info info) throws Exception {
			if(info instanceof ContactUpdated) {
				ContactUpdated cu = (ContactUpdated)info;
				Contact contact = cu.get();
				Node form = contactsContent.lookup("#" + contact.id());
				if(form != null) {
					Label symbol = (Label) form.lookup("#symbol");
					Label username = (Label) form.lookup("#username");
					Label liberaddress = (Label) form.lookup("#liberaddress");
					Label invitation = (Label) form.lookup("#invitation");
					if((cu.complete() || cu.photo())) {
						if (contact.info().hasPhoto()) {
							symbol.setText(null);
							symbol.setGraphic(ProfileController.instanciateImageView(
									new Image(new ByteArrayInputStream(contact.info().photoBytes())), 40
							));
						} else {
							symbol.setText("\ud83d\ude09");
						}
					}
					if(cu.complete() || cu.firstname() || cu.lastname()) {
						TextFlow tf = new TextFlow();
						Text t1 = new Text();
						if(contact.online()) {
							t1.setText(DiscussionController.onlineString);
							t1.setFill(Color.GREEN);
						} else {
							t1.setText(DiscussionController.offlineString);
							t1.setFill(Color.RED);
						}
						Text t2 = new Text(" " + contact.appellation());
						tf.getChildren().addAll(t1, t2);
						//tf.setStyle("-fx-font-size:15");
						//tf.setStyle("-fx-font-weight:bold");
						username.setText(null);
						username.setGraphic(tf);
					}
					liberaddress.setText(contact.liberaddress().toString());
					if(cu.complete() || cu.status())
						invitation.setText(contact.info().status());
				}
				return;
			}
			if(info instanceof InlinkDeleted || info instanceof LinkOfferReceived) {
				clearInlinks();
				showInlinks();
				tabs.getSelectionModel().select(WorkForm.INLINKS);
				return;
			}
			if(info instanceof OutlinkAccepted) {
				clearOutlinks();
				clearContacts();
				showOutlinks();
				showContacts();
				tabs.getSelectionModel().select(WorkForm.CONTACTS);
				return;
			}
			if(info instanceof ContactDeleted) {
				clearContacts();
				showContacts();
				tabs.getSelectionModel().select(WorkForm.CONTACTS);
				return;
			}
			if(info instanceof OutlinkDeleted) {
				clearOutlinks();
				showOutlinks();
				tabs.getSelectionModel().select(WorkForm.OUTLINKS);
			}
		}
	}

	@FXML
	private Label thumbnail;
	@FXML
	private Label appellation;
	@FXML
	private Label info;
	@FXML
	private Button menuButton;
	@FXML
	private ContextMenu contextMenu;
	@FXML
	private TabPane tabs;
	@FXML
	private Tab contactsTab;
	@FXML
	private VBox contactsContent;
	@FXML
	private Tab inlinksTab;
	@FXML
	private VBox inlinksContent;
	@FXML
	private Tab outlinksTab;
	@FXML
	private VBox outlinksContent;

	private Label getHelp() {
		Text text1 = new Text("Votre liber-adresse est:\n\n");
		Text text2 = new Text(Libersaurus.current.account().liberaddress().toString());
		Text text3 = new Text("\n\nVotre liber-adresse est votre identifiant sur le réseau Libersaurus.\n");
		Text text4 = new Text("\nPour discuter avec quelqu'un, vous devez connaître sa liber-adresse, ou lui envoyer votre liber-adresse via un autre moyen de communication.\n");
		Text text5 = new Text("\nPour créer un contact, cliquez sur le bouton des paramètres en haut à droite et choisissez \"Nouveau contact\". Vous devrez insérer la liber-adresse du contact ainsi qu'un message d'invitation.\n");
		text2.setStyle("-fx-font-weight:bold");
		TextFlow textFlow = new TextFlow();
		textFlow.getChildren().addAll(text1, text2, text3, text4, text5);
		Label label = new Label();
		label.setWrapText(true);
		label.setPadding(new Insets(10, 10, 10, 10));
		label.setGraphic(textFlow);
		return label;
	}
	private void clearContacts() {
		contactsContent.getChildren().clear();
	}
	private void clearInlinks() {
		inlinksContent.getChildren().clear();
	}
	private void clearOutlinks() {
		outlinksContent.getChildren().clear();
	}
	private void showContacts() throws Exception {
		if(Libersaurus.current.contacts().isEmpty())
			contactsContent.getChildren().add(getHelp());
		else for(Contact contact: Libersaurus.current.contacts()) {
			Parent form = new LinkForm().root();
			Label symbol = (Label) form.lookup("#symbol");
			Label username = (Label) form.lookup("#username");
			Label liberaddress = (Label) form.lookup("#liberaddress");
			Label invitation = (Label) form.lookup("#invitation");
			if(contact.info().hasPhoto()) {
				symbol.setText(null);
				symbol.setGraphic(ProfileController.instanciateImageView(
						new Image(new ByteArrayInputStream(contact.info().photoBytes())), 40
				));
			} else {
				symbol.setText("\ud83d\ude09");
			}
			TextFlow tf = new TextFlow();
			Text t1 = new Text();
			if(contact.online()) {
				t1.setText(DiscussionController.onlineString);
				t1.setFill(Color.GREEN);
			} else {
				t1.setText(DiscussionController.offlineString);
				t1.setFill(Color.RED);
			}
			Text t2 = new Text(" " + contact.appellation());
			tf.getChildren().addAll(t1, t2);
			username.setText(null);
			username.setGraphic(tf);
			liberaddress.setText(contact.liberaddress().toString());
			invitation.setText(contact.info().status());
			Button button = new Button();
			form.setId(String.valueOf(contact.id()));
			button.setGraphic(form);
			button.setOnAction((event) -> {
				try {
					GUI.current.load(new DiscussionForm(contact));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			contactsContent.getChildren().add(button);
			button.setMaxWidth(Double.MAX_VALUE);
		}
	}
	private void showInlinks() throws Exception {
		if(Libersaurus.current.inlinks().isEmpty())
			inlinksContent.getChildren().add(getHelp());
		else for(InMessage message: Libersaurus.current.inlinks()) {
			Parent form = new LinkForm().root();
			Label symbol = (Label) form.lookup("#symbol");
			Label username = (Label) form.lookup("#username");
			Label liberaddress = (Label) form.lookup("#liberaddress");
			Label invitation = (Label) form.lookup("#invitation");
			symbol.setText("\u2709");
			username.setText(message.sender().username());
			liberaddress.setText(message.sender().liberaddress().toString());
			invitation.setText(message.decodedContent());
			Button button = new Button();
			button.setGraphic(form);
			button.setOnAction((event) -> {
				try {
					GUI.current.load(new InlinkForm(message));
				} catch(Exception e) {
					e.printStackTrace();
				}
			});
			inlinksContent.getChildren().add(button);
			button.setMaxWidth(Double.MAX_VALUE);
		}
	}
	private void showOutlinks() throws Exception {
		if(Libersaurus.current.outlinks().isEmpty())
			outlinksContent.getChildren().add(getHelp());
		else for(OutMessage message: Libersaurus.current.outlinks()) {
			Parent form = new LinkForm().root();
			Label symbol = (Label) form.lookup("#symbol");
			Label username = (Label) form.lookup("#username");
			Label liberaddress = (Label) form.lookup("#liberaddress");
			Label invitation = (Label) form.lookup("#invitation");
			symbol.setText("\u2708");
			username.setText(message.recipient().username());
			liberaddress.setText(message.recipient().liberaddress().toString());
			invitation.setText(message.decodedContent());
			Button button = new Button();
			button.setGraphic(form);
			button.setOnAction((event) -> {
				try {
					GUI.current.load(new OutlinkForm(message));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			outlinksContent.getChildren().add(button);
			button.setMaxWidth(Double.MAX_VALUE);
		}
	}
	public void init(WorkForm form) throws Exception {
		int tabIndex = form.getTabIndex();
		GUI.current.notifier().setInformer(new WorkInformer());
		Account account = Libersaurus.current.account();
		tabs.getSelectionModel().select(tabIndex);
		appellation.setText(account.appellation());
		info.setText(account.liberaddress().toString());
		if(account.info().hasPhoto()) {
			Image image = new Image(new ByteArrayInputStream(account.info().photoBytes()));
			ImageView imageView = ProfileController.instanciateImageView(image);
			thumbnail.setText(null);
			thumbnail.setGraphic(imageView);
		}
		// Contacts.
		showContacts();
		// Inlinks.
		showInlinks();
		// Outlinks.
		showOutlinks();
	}

	@FXML
	void loadProfile(ActionEvent event) throws Exception {
		GUI.current.load(new ProfileForm());
	}

	@FXML
	void newContact(ActionEvent event) throws Exception {
		GUI.current.load(new NewContactForm());
	}

	@FXML
	void logout(ActionEvent event) throws Exception {
		Question question = new Question();
		question.setTitle("Déconnexion");
		question.setQuestion("Voulez-vous vraiment vous déconnecter ?");
		question.setPositiveLabel("Se déconnecter");
		question.setAction(() -> {
			LogoutCommand command = new LogoutCommand();
			if(command.run()) {
				GUI.current.fullBack();
			}
		});
		question.show();
	}

	@FXML
	void deleteAccount(ActionEvent event) throws Exception {
		Question question = new Question();
		question.setTitle("Suppression du compte.");
		question.setQuestion("Voulez-vous vraiment supprimer ce compte ?\nCette action est irréversibe !");
		question.setPositiveLabel("Supprimer définitivement le compte");
		question.setAction(() -> {
			DeleteAccountCommand command = new DeleteAccountCommand();
			if(command.run()) {
				GUI.current.load(new ValidateDeletionForm());
			}
		});
		question.show();
	}

	@FXML
	void menu(ActionEvent event) {
		contextMenu.show(menuButton, Side.BOTTOM, 0, 0);
	}


}
