package liber.gui.form;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import liber.Libersaurus;
import liber.data.Account;

import java.io.ByteArrayInputStream;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
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

	@Override public void init(Parent root) {
		TabPane tabs = (TabPane) root.lookup("#tabs");
		Label thumbnail = (Label)root.lookup("#thumbnail");
		Label appellation = (Label)root.lookup("#appellation");
		Label info = (Label)root.lookup("#info");
		Account account = Libersaurus.current.account();
		tabs.getSelectionModel().select(tabIndex);
		appellation.setText(account.appellation());
		info.setText(account.liberaddress().toString());
		if(account.info().hasPhoto()) {
			ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(account.info().photoBytes())));
			imageView.setPreserveRatio(true);
			imageView.setSmooth(true);
			imageView.setFitWidth(80);
			imageView.setFitHeight(80);
			thumbnail.setText(null);
			thumbnail.setGraphic(imageView);
		}
	}
}
