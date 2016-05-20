package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import liber.Utils;
import liber.command.UpdateInfoCommand;
import liber.data.Account;
import liber.enumeration.CommandField;
import liber.enumeration.ContactData;
import liber.gui.GUI;
import liber.gui.form.ProfileForm;
import liber.notification.Notification;

import java.io.*;

public class ProfileController {
	static public ImageView instanciateImageView(Image image, int size) {
		ImageView imageView = new ImageView(image);
		imageView.setPreserveRatio(true);
		imageView.setSmooth(true);
		imageView.setFitWidth(size);
		imageView.setFitHeight(size);
		return imageView;
	}
	static public ImageView instanciateImageView(Image image) {
		return instanciateImageView(image, 80);
	}

	@FXML
	private TextField firstnameLabel;

	@FXML
	private TextField lastnameLabel;

	@FXML
	private Label photoLabel;

	@FXML
	private TextField statusLabel;

	@FXML
	private Label profileTitle;

	private StringBuilder photoContent;

	@FXML
	void cancel(ActionEvent event) throws Exception {
		GUI.current.back();
	}

	@FXML
	void deletePhoto(ActionEvent event) {
		photoLabel.setText("Aucune photo définie.");
		photoLabel.setGraphic(null);
		photoContent = null;
	}

	@FXML
	void loadPhoto(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose your photo profile ...");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png","*.jpg","*.jpeg", "*.gif"));
		File file = GUI.current.getFile(fileChooser);
		if(file != null) {
			try {
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int read;
				do {
					read = fis.read(buffer);
					if(read != -1) baos.write(buffer, 0, read);
				} while(read != -1);
				photoContent = new StringBuilder(Utils.encodeBytes(baos.toByteArray()));
				Image image = new Image(new ByteArrayInputStream(baos.toByteArray()));
				ImageView imageView = instanciateImageView(image);
				photoLabel.setText(null);
				photoLabel.setGraphic(imageView);
				fis.close();
				baos.close();
			} catch(Exception e) {
				Notification.bad("Impossible d'ouvrir la photo indiquée !");
			}
		}
	}

	@FXML
	void saveProfile(ActionEvent event) throws Exception {
		String fn = firstnameLabel.getText();
		String ln = lastnameLabel.getText();
		String st = statusLabel.getText();
		if(fn != null) fn = fn.trim();
		if(ln != null) ln = ln.trim();
		if(st != null) st = st.trim();
		Notification.silence();
		UpdateInfoCommand command = new UpdateInfoCommand();
		//firstname
		command.put(CommandField.data, ContactData.firstname.toString());
		command.put(CommandField.value, fn);
		command.run();
		//lastname
		command.put(CommandField.data, ContactData.lastname.toString());
		command.put(CommandField.value, ln);
		command.run();
		//status
		command.put(CommandField.data, ContactData.status.toString());
		command.put(CommandField.value, st);
		command.run();
		//photo
		// TODO: À optimiser: trop de copies de string pour la sauvegarde de la photo.
		if(photoLabel.getText() != null || photoContent != null) {
			command.put(CommandField.data, ContactData.photo.toString());
			command.put(CommandField.value, photoContent == null ? null : photoContent.toString());
			command.run();
		}
		Notification.speak();
		Notification.good("Profil sauvegardé.");
		GUI.current.back();
	}

	public void init(ProfileForm form) {
		Account account = form.account();
		firstnameLabel.setText(account.info().firstname());
		lastnameLabel.setText(account.info().lastname());
		statusLabel.setText(account.info().status());
		if(account.info().hasPhoto()) {
			ImageView imageView = ProfileController.instanciateImageView(
					new Image(new ByteArrayInputStream(account.info().photoBytes()))
			);
			photoLabel.setText(null);
			photoLabel.setGraphic(imageView);
		}
	}
}
