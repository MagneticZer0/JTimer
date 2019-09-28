package org.jtimer.CommandLine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Console extends OutputStream {

	private TextArea textArea = new TextArea();
	private PrintStream stream = new PrintStream(this);

	@Override
	public void write(int b) throws IOException {
		Platform.runLater(() -> {
			textArea.appendText(String.valueOf((char) b)); // Add the byte of data
			textArea.selectEnd(); // Move to end
		});
	}

	public PrintStream getStream() {
		return stream;
	}
}
