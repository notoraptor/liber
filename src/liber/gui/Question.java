package liber.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import liber.Action;
import liber.gui.form.QuestionForm;

public class Question {
	private String title;
	private String question;
	private String positiveLabel;
	private String negativeLabel;
	private Action action;
	public Question() {
		title = "Question";
		positiveLabel = "Oui";
		negativeLabel = "Annuler";
	}
	public void setTitle(String theTitle) {
		title = theTitle;
	}
	public void setQuestion(String theQuestion) {
		question = theQuestion;
	}
	public void setPositiveLabel(String theLabel) {
		positiveLabel = theLabel;
	}
	public void setNegativeLabel(String theLabel) {
		negativeLabel = theLabel;
	}
	public void setAction(Action theAction) {
		action = theAction;
	}
	public void show() throws Exception {
		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		Parent root = new QuestionForm().root();
		Label messageLabel = (Label) root.lookup("#messageLabel");
		Label detailsLabel = (Label) root.lookup("#detailsLabel");
		Button cancelButton = (Button) root.lookup("#cancelButton");
		Button okButton = (Button) root.lookup("#okButton");
		messageLabel.setText(title);
		detailsLabel.setText(question);
		cancelButton.setText(negativeLabel);
		okButton.setText(positiveLabel);
		cancelButton.setOnAction((event) -> dialogStage.close());
		okButton.setOnAction(event -> {
			dialogStage.close();
			if(action != null) {
				try {
					action.execute();
				} catch(Exception e) {
					e.printStackTrace(System.err);
				}
			}
		});
		dialogStage.setScene(new Scene(root));
		dialogStage.setTitle(title);
		dialogStage.show();
	}
}
