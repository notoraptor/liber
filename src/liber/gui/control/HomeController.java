package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import liber.Libersaurus;
import liber.gui.form.CreateAccountForm;
import liber.gui.GUI;
import liber.gui.form.HomeForm;
import liber.gui.form.LoginForm;
import liber.notification.Info;
import liber.notification.Informer;
import liber.notification.info.LibersaurusLoaded;

public class HomeController {
	class HomeInformer implements Informer {
		@Override public void inform(Info info) {
			if(info instanceof LibersaurusLoaded) {
				loading.setText(null);
				createButton.setDisable(false);
				loginButton.setDisable(false);
				homeForm.setContext(((LibersaurusLoaded)info).get());
			}
		}
	}
	@FXML
	private Label loading;
	@FXML
	private Button createButton;
	@FXML
	private Button loginButton;

	private HomeForm homeForm;
	public void init(HomeForm form) {
		homeForm = form;
		Libersaurus context = homeForm.getContext();
		if(context == null) {
			GUI.current.notifier().setInformer(new HomeInformer());
			loading.setText("Chargement en cours ...");
			createButton.setDisable(true);
			loginButton.setDisable(true);
		} else {
			loading.setText(null);
			createButton.setDisable(false);
			loginButton.setDisable(false);
		}
	}
	@FXML
	void createAccount(ActionEvent event) throws Exception {
		GUI.current.load(new CreateAccountForm());
	}
	@FXML
	void login(ActionEvent event) throws Exception {
		GUI.current.load(new LoginForm());
	}
}
