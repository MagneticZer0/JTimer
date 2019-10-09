package org.jtimer.CommandLine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Console extends OutputStream {

	private TextArea textArea = new TextArea();
	private PrintStream stream = new PrintStream(this);

	public Console() {
		Stage mainStage = new Stage();
		Pane pane = new Pane();
		mainStage.setTitle("Console");
		mainStage.setScene(new Scene(pane, 435, 265));
	}

	@Override
	public void write(int b) throws IOException {
		Platform.runLater(() -> {
			textArea.appendText(String.valueOf((char) b)); // Add the byte of data
			textArea.selectEnd(); // Move to end
		});
	}

	public void clear() {
		Platform.runLater(() -> {
			textArea.clear();
		});
	}

	public PrintStream getStream() {
		return stream;
	}
}
