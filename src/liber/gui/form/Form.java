package liber.gui.form;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

abstract public class Form {
	private String filename;
	private String title;
	public Form(String title) {
		this.filename = name() + ".fxml";
		this.title = title;
	}
	final public String title() {
		return title;
	}
	final public Parent root() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/liber/gui/fxml/"  + filename));
		Parent root = loader.load();
		if(control(loader)) {
			System.out.println("Contrôleur exécuté pour " + name() + '.');
		} else {
			System.err.println("Pas de contrôleur pour " + name() + '.');
		}
		return root;
	}
	abstract protected FormName name();
	abstract protected boolean control(FXMLLoader loader) throws Exception;
}
