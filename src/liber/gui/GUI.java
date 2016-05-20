package liber.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import liber.Libersaurus;
import liber.data.Contact;
import liber.gui.form.Form;
import liber.gui.form.HomeForm;
import liber.gui.form.WorkForm;
import liber.notification.Informer;
import liber.notification.Notification;
import liber.notification.info.LibersaurusLoaded;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;

public class GUI extends Application {
	static public GUI current;
	private Libersaurus instance;
	private GuiNotifier notifier;
	private LinkedList<Form> history;
	private Stage stage;
	private boolean firstTime;
	private boolean messageDisplayed;
	private TrayIcon trayIcon;
	public void setInformer(Informer informer) {
		notifier.setInformer(informer);
	}
	public void setCurrentContact(Contact contact) {
		notifier.setCurrentContact(contact);
	}
	public File getFile(FileChooser fileChooser) {
		return fileChooser.showOpenDialog(stage);
	}
	private void loadFirst(Form form) throws Exception {
		Scene scene = new Scene(form.root());
		stage.setTitle(form.title());
		stage.setScene(scene);
		history.push(form);
	}
	public void load(Form form) throws Exception {
		notifier.setInformer(null);
		notifier.setCurrentContact(null);
		stage.setTitle(form.title());
		stage.getScene().setRoot(form.root());
		history.push(form);
	}
	public void back() throws Exception {
		if(!history.isEmpty()) history.pop();
		if(!history.isEmpty()) load(history.pop());
	}
	public void fullBack() throws Exception {
		Form form = null;
		while (!history.isEmpty()) form = history.pop();
		if(form != null) load(form);
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
	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) {
			System.err.println("Erreur fatale.");
			e.printStackTrace();
			if(Libersaurus.current != null) try {
				Libersaurus.current.close();
			} catch (Exception f) {
				System.err.println("Impossible de fermer totalement Libersaurus après une erreur fatale.");
				f.printStackTrace();
			}
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
		/*
		* L'utilisation d'un processus permet d'afficher l'interface graphique
		* en attendant que le programme soit entièrement chargé.
		* */
		new Thread(() -> {
			try {
				instance = new Libersaurus();
				Notification.info(new LibersaurusLoaded(instance));
				try {
					createTrayIcon(stage);
				} catch (Throwable e) {
					System.err.println("Impossible d'installer Libersaurus dans une barre de tâches.");
					e.printStackTrace();
				}
			} catch (Exception e) {
				System.err.println("Erreur pendant l'instanciation de Libersaurus.");
				e.printStackTrace();
			}
		}).start();
		stage.show();
	}
	@Override
	public void stop() throws Exception {
		if(instance != null) {
			System.err.println("Fermeture de Libersaurus.");
			instance.close();
			instance = null;
		}
		super.stop();
	}
	public void updateTitle() {
		if(trayIcon != null) if(instance != null && instance.loaded()) {
			String title = instance.account().appellation() + " - Libersaurus";
			trayIcon.setToolTip(title);
		} else {
			trayIcon.setToolTip("Libersaurus");
		}
	}
	public void showQuitQuestion() {
		Question quitQuestion = new Question();
		quitQuestion.setTitle("Quitter Libersaurus");
		quitQuestion.setQuestion("Voulez-vous quitter Libersaurus ?");
		quitQuestion.setPositiveLabel("Quitter (vous serez déconnecté)");
		quitQuestion.setPositiveAction(() -> {
			forceEnd();
		});
		quitQuestion.setNegativeLabel("Réduire dans la barre des tâches");
		quitQuestion.setNegativeAction(() -> {
			if (stage.isShowing())
				stage.hide();
			else {
				if (!messageDisplayed)
					stage.show();
				messageDisplayed = false;
			}
		});
		try {
			quitQuestion.show();
		} catch (Exception e) {
			forceEnd();
		}
	}
	private void closeSystemTray() {
		if(SystemTray.isSupported() && trayIcon != null) {
			SystemTray.getSystemTray().remove(trayIcon);
		}
	}
	private void forceEnd() {
		stage.hide();
		closeSystemTray();
		try {
			stop();
		} catch (Exception ignored) {}
		Platform.exit();
		//System.exit(0);
	}
	private void createTrayIcon(final Stage stage) throws Throwable {
		if (SystemTray.isSupported()) {
			// load an image
			URL url = getClass().getResource("/liber/resource/image/libericon.png");
			java.awt.Image image = ImageIO.read(url);
			// create a action listener to listen for default action executed on the tray icon
			final ActionListener closeListener = (actionEvent) -> {
				Question question = new Question();
				question.setTitle("Quitter Libersaurus");
				question.setQuestion("Voulez-vous vraiment quitter Libersaurus?\nVous serez automatiquement déconnecté.");
				question.setPositiveLabel("Quitter Libersaurus");
				question.setPositiveAction(() -> {
					forceEnd();
				});
				Platform.runLater(() -> {
					if(!instance.loaded())
						forceEnd();
					else try {
						question.show();
					} catch (Exception e) {
						e.printStackTrace();
						forceEnd();
					}
				});
			};
			final ActionListener showListener = (actionEvent) -> Platform.runLater(() -> stage.show());
			// create a popup menu
			PopupMenu popup = new PopupMenu();
			MenuItem showItem = new MenuItem("Afficher Libersaurus");
			MenuItem closeItem = new MenuItem("Quitter Libersaurus");
			showItem.addActionListener(showListener);
			closeItem.addActionListener(closeListener);
			popup.add(showItem);
			popup.add(closeItem);
			/// ... add other items
			// construct a TrayIcon
			trayIcon = new TrayIcon(image, "Libersaurus", popup);
			// set the TrayIcon properties
			trayIcon.addActionListener((actionEvent) -> {
				Platform.runLater(() -> {
					if(stage.isShowing())
						stage.hide();
					else {
						if(!messageDisplayed)
							stage.show();
						messageDisplayed = false;
					}
				});
			});
			// ...
			// add the tray image
			firstTime = true;
			try {
				Platform.setImplicitExit(false);
				SystemTray.getSystemTray().add(trayIcon);
				stage.setOnCloseRequest((windowEvent) -> hide(stage));
			} catch (AWTException e) {
				Platform.setImplicitExit(true);
				throw e;
			}
			// ...
		}
	}
	private void hide(final Stage stage) {
		Platform.runLater(() -> {
			if (SystemTray.isSupported()) {
				if(instance.loaded()) {
					stage.hide();
					showProgramIsMinimizedMsg();
				} else {
					closeSystemTray();
					Platform.exit();
				}
			} else {
				closeSystemTray();
				Platform.exit();
				//System.exit(0);
			}
		});
	}
	private void showProgramIsMinimizedMsg() {
		if (firstTime) {
			trayIcon.displayMessage("Libersaurus",
					"Libersaurus est réduit dans la barre des tâches.",
					TrayIcon.MessageType.INFO);
			messageDisplayed = true;
			firstTime = false;
		}
	}
}
