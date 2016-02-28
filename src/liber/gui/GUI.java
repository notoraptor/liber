package liber.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import liber.Libersaurus;
import liber.gui.form.Form;
import liber.gui.form.HomeForm;
import liber.gui.form.WorkForm;
import liber.notification.Notification;

import java.util.LinkedList;

public class GUI extends Application {
	static public GUI current;
	private GuiNotifier notifier;
	private LinkedList<Form> history;
	private Stage stage;
	public GuiNotifier notifier() {
		return notifier;
	}
	public Stage stage() {
		return stage;
	}
	private void loadFirst(Form form) throws Exception {
		Scene scene = new Scene(form.root());
		stage.setTitle(form.title());
		stage.setScene(scene);
		history.push(form);
	}
	public void load(Form form) throws Exception {
		notifier.reset();
		stage.setTitle(form.title());
		stage.getScene().setRoot(form.root());
		history.push(form);
	}
	public void back() throws Exception {
		if(!history.isEmpty()) history.pop();
		if(!history.isEmpty()) load(history.pop());
	}
	public Form getBack() {
		Form form = null;
		if(!history.isEmpty()) history.pop();
		if(!history.isEmpty()) form = history.pop();
		return form;
	}
	public WorkForm findWorkForm() {
		Form form = null;
		while(form == null && !history.isEmpty()) {
			if(history.getFirst() instanceof HomeForm) break;
			if(history.getFirst() instanceof WorkForm) {
				form = history.pop();
			} else {
				history.pop();
			}
		}
		return (form == null ? new WorkForm() : (WorkForm)form);
	}
	public void fullBack() throws Exception {
		Form form = null;
		while (!history.isEmpty()) form = history.pop();
		if(form != null) load(form);
	}
	public static void main(String[] args) {
		try (
			Libersaurus instance = new Libersaurus()
		) {
			launch(args);
		} catch (Exception e) {
			System.err.println("Erreur fatale.");
			e.printStackTrace();
		}
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		current = this;
		notifier = new GuiNotifier();
		history = new LinkedList<>();
		stage = primaryStage;
		stage.setMinWidth(325);
		stage.setMinHeight(500);
		Notification.setManager(notifier);
		loadFirst(new HomeForm());
		stage.show();
	}
}
