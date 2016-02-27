package liber.gui.form;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import liber.Libersaurus;

import java.io.ByteArrayInputStream;

public class ValidateCreationForm extends Form {
	private boolean fromConnexion;
	public ValidateCreationForm() {
		super("Valider la création du compte");
	}
	public ValidateCreationForm(boolean fromConnexion) {
		this();
		this.fromConnexion = fromConnexion;
	}
	@Override
	public void init(Parent root) throws Exception {
		if(fromConnexion) {
			Label infoLabel = (Label) root.lookup("#infoLabel");
			infoLabel.setText("Votre compte est en attente de validation.");
		}
		byte[] data = Libersaurus.current.features().getCaptchaImageForCreation();
		if(data != null) {
			ImageView imageLabel = (ImageView) root.lookup("#imageLabel");
			imageLabel.setImage(new Image(new ByteArrayInputStream(data)));
		}
	}
}
