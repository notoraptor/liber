package liber.gui.form;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import liber.Libersaurus;
import liber.data.Account;
import liber.gui.control.Controller;

import java.io.ByteArrayInputStream;

public class WorkForm extends Form {
	static public final int CONTACTS = 0;
	static public final int INLINKS = 1;
	static public final int OUTLINKS = 2;
	static public final int COUNT = 3;
	private int tabIndex;
	public void setTabIndex(int tab) {
		tabIndex = tab > -1 && tab < COUNT ? tab : CONTACTS;
	}
	public WorkForm(int tab) {
		super("Bienvenue, " + Libersaurus.current.account().appellation() + " !");
		setTabIndex(tab);
	}
	public WorkForm() {
		this(CONTACTS);
	}

	@Override public void init(Controller controller) throws Exception {
		controller.load(tabIndex);
	}
}
