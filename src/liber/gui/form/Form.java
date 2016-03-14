package liber.gui.form;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.lang.reflect.Method;

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
		if(oc != null && initController(oc))
			System.out.println("Contrôleur exécuté pour " + getName() + '.');
		else {
			System.err.println("Pas de contrôleur pour " + getName() + '.');
			//init(root);
		}
		return root;
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
	private boolean initController(Object potentialController) {
		String classname = getClass().getName();
		int position = classname.lastIndexOf('.');
		if(position > 0)
			classname = classname.substring(position + 1);
		if(classname.endsWith("Form"))
			classname = classname.substring(0, classname.length() - "Form".length());
		String controllerClassname = "liber.gui.control." + classname + "Controller";
		try {
			Class controllerClass = Class.forName(controllerClassname);
			if(controllerClass.isAssignableFrom(potentialController.getClass())) {
				Method method = controllerClass.getMethod("init", getClass());
				method.invoke(potentialController, this);
				return true;
			}
		} catch(Throwable ignored) {}
		return false;
	}
}
