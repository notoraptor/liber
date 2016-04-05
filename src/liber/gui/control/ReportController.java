package liber.gui.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import liber.card.LibercardReport;
import liber.data.Contact;
import liber.data.InMessage;
import liber.data.OutMessage;
import liber.gui.GUI;
import liber.gui.RichText;
import liber.gui.form.ReportForm;
import liber.gui.form.WorkForm;

public class ReportController {

	@FXML
	private VBox content;

	@FXML
	void done(ActionEvent event) throws Exception {
		GUI.current.load(new WorkForm());
	}

	// TODO: Mettre en forme les textes du rapport
	// TODO (ajouter des retours � la ligne, mettre les liber-adresses et les noms des contacts en gras, etc.).
	public void init(ReportForm reportForm) {
		LibercardReport report = reportForm.report();
		for(Contact contact: report.contacts()) {
			RichText richText = new RichText();
			richText.append("Le contact ")
					.bold(contact.appellation())
					.append(" situ� � la liber-adresse ")
					.boldItalic(contact.liberaddress())
					.append(" a �t� supprim� car son compte n'a pas �t� trouv� sur Internet. ")
					.append("Il a probablement supprim� son compte.");
			if(report.historySaved(contact)) {
				richText.append(" L'historique de votre discussion est sauvegard�e dans le fichier ")
						.boldItalic(report.historyFilename(contact))
						.append(".");
			}
			content.getChildren().add(richText.label());
		}
		for(InMessage inlink: report.inlinks()) {
			Contact contact = inlink.sender();
			RichText text = new RichText();
			text.append("La demande que vous avez re�ue de l'utilisateur ")
				.bold(contact.appellation())
				.append(" situ� � la liber-adresse ")
				.boldItalic(contact.liberaddress())
				.append(" a �t� supprim�e car le compte de cet utilisateur n'a pas �t� trouv� sur Internet. ")
				.append("Il a probablement supprim� son compte.");
			content.getChildren().add(text.label());
		}
		for(OutMessage outlink: report.outlinks()) {
			Contact contact = outlink.recipient();
			RichText text = new RichText();
			text.append("La demande que vous avez envoy�e � l'utilisateur ")
				.bold(contact.appellation())
				.append(" situ� � la liber-adresse ")
				.boldItalic(contact.liberaddress())
				.append(" a �t� supprim�e car le compte de cet utilisateur n'a pas �t� trouv� sur Internet. ")
				.append("Il a probablement supprim� son compte.");
			content.getChildren().add(text.label());
		}
	}
}
