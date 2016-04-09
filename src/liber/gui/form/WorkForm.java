package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.Libersaurus;
import liber.gui.control.WorkController;

public class WorkForm extends Form {
	static public final int CONTACTS = 0;
	static public final int INLINKS = 1;
	static public final int OUTLINKS = 2;
	static public final int IGNORED_CONTACTS = 3;
	static public final int COUNT = 4;
	private int tabIndex;
	public void setTabIndex(int tab) {
		tabIndex = tab > -1 && tab < COUNT ? tab : CONTACTS;
	}
	public int getTabIndex() {
		return tabIndex;
	}
	public WorkForm(int tab) {
		super("Bienvenue, " + Libersaurus.current.account().appellation() + " !");
		setTabIndex(tab);
	}
	public WorkForm() {
		this(CONTACTS);
	}
	@Override
	protected FormName name() {
		return FormName.work;
	}
	@Override
	protected boolean control(FXMLLoader loader) throws Exception {
		WorkController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
