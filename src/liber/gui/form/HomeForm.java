package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.Libersaurus;
import liber.gui.control.HomeController;

public class HomeForm extends Form {
	private Libersaurus context;
	public HomeForm() {
		super("Home");
	}
	public void setContext(Libersaurus theContext) {
		context = theContext;
	}
	public Libersaurus getContext() {
		return context;
	}
	@Override
	protected FormName name() {
		return FormName.home;
	}
	@Override
	protected boolean control(FXMLLoader loader) {
		HomeController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
