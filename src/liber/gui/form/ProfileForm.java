package liber.gui.form;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import liber.Libersaurus;
import liber.data.Account;
import liber.gui.control.ProfileController;

import java.io.ByteArrayInputStream;

/**
 * Created by HPPC on 21/02/2016.
 */
public class ProfileForm extends Form {
	public ProfileForm() {
		super("Profile of " + Libersaurus.current.account().appellation());
	}
	@Override public void init(Parent root) {
		Account account = Libersaurus.current.account();
		TextField firstnameLabel = (TextField) root.lookup("#firstnameLabel");
		TextField lastnameLabel = (TextField) root.lookup("#lastnameLabel");
		Label photoLabel = (Label) root.lookup("#photoLabel");
		TextField statusLabel = (TextField) root.lookup("#statusLabel");
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
