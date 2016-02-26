package liber.gui.form;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import liber.gui.control.Controller;

/**
 * Created by HPPC on 21/02/2016.
 */
public class Form {
	private String filename;
	private String title;
	public Form(String title) {
		this.filename = getName() + ".fxml";
		this.title = title;
	}
	final public String filename() {
		return filename;
	}
	final public String title() {
		return title;
	}
	final public Parent root() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/liber/gui/fxml/"  + filename));
		Parent root = loader.load();
		Object oc = loader.getController();
		if(oc instanceof Controller)
			init((Controller)oc);
		else
			init(root);
		return root;
	}
	public void init(Parent root) throws Exception {
		// Do anything you want with root here.
	}
	public void init(Controller controller) throws Exception {
		//...
	}
	private String getName() {
		String classname = getClass().getName();
		int position = classname.lastIndexOf('.');
		if(position > 0) classname = classname.substring(position + 1);
		if (!classname.startsWith("Form")) {
			classname = Character.toLowerCase(classname.charAt(0))
					+ classname.substring(1, classname.length() - "Form".length());
		}
		return classname;
	}
}
