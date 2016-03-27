package liber.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class RichText {
	private Label label;
	private TextFlow textFlow;
	public RichText() {
		label = new Label();
		textFlow = new TextFlow();
		label.setWrapText(true);
		label.setPadding(new Insets(5, 5, 5, 5));
		label.setGraphic(textFlow);
	}
	public RichText append(String str) {
		Text text = new Text(str);
		textFlow.getChildren().add(text);
		return this;
	}
	public RichText append(Object object) {
		Text text = new Text(object.toString());
		textFlow.getChildren().add(text);
		return this;
	}
	public RichText bold(String str) {
		Text text = new Text(str);
		text.setStyle("-fx-font-weight:bold");
		textFlow.getChildren().add(text);
		return this;
	}
	public RichText bold(Object object) {
		Text text = new Text(object.toString());
		text.setStyle("-fx-font-weight:bold");
		textFlow.getChildren().add(text);
		return this;
	}
	public RichText boldItalic(Object object) {
		Text text = new Text(object.toString());
		text.setStyle("-fx-font-weight:bold; -fx-font-style:italic;");
		textFlow.getChildren().add(text);
		return this;
	}
	public Label label() {
		return label;
	}
}
