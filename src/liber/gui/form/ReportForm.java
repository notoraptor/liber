package liber.gui.form;

import javafx.fxml.FXMLLoader;
import liber.card.LibercardReport;
import liber.gui.control.ReportController;

public class ReportForm extends Form {
	private LibercardReport report;
	public ReportForm(LibercardReport report) {
		super("Rapport");
		this.report = report;
	}
	public LibercardReport report() {
		return report;
	}
	@Override
	protected FormName name() {
		return FormName.report;
	}
	@Override
	protected boolean control(FXMLLoader loader) throws Exception {
		ReportController controller = loader.getController();
		controller.init(this);
		return true;
	}
}
