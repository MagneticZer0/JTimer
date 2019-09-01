package org.jtimer.Exceptions.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jtimer.Readability.If;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PopupDialogue {

	private Stage popupStage;
	private TextArea textArea = new TextArea();
	private Button btnNumber1;
	private Button btnNumber2;

	/**
	 * Used to create popup dialogues, for now only used for logging errors.
	 * 
	 * @param title   The title for the PopupDialogue
	 * @param btn1    If the first button is visible
	 * @param btn1txt The text of the first button
	 * @param btn2    If the second button is visible
	 * @param btn2txt The text of the second button
	 */
	public PopupDialogue(String title, boolean btn1, String btn1txt, boolean btn2, String btn2txt) {
		Platform.runLater(() -> {
			popupStage = new Stage();
			Pane pane = new Pane();
			popupStage.setTitle(title);
			popupStage.setResizable(false);
			popupStage.setAlwaysOnTop(true);
			popupStage.setScene(new Scene(pane, 435, 265));
			popupStage.setOnCloseRequest(e -> {
				popupStage.hide();
			});

			textArea.setLayoutX(10);
			textArea.setLayoutY(11);
			textArea.setPrefWidth(424);
			textArea.setPrefHeight(219);
			textArea.setEditable(false);
			textArea.appendText("Well, looks like I've run into a problem!\n");
			textArea.appendText("Ignore and exit buttons will create a log file!\n");
			textArea.appendText("Please create a bug report and upload the file created, thanks!\n");
			pane.getChildren().add(textArea);

			if (btn1) {
				btnNumber1 = new Button(btn1txt);
				btnNumber1.setOnMouseClicked(e -> {
					CreateLog();
					popupStage.hide();
				});
				btnNumber1.setLayoutX(10);
				btnNumber1.setLayoutY(237);
				btnNumber1.setPrefWidth(new If<Double>(btn1 ^ btn2).Then(424d).Else(210d));
				btnNumber1.setPrefHeight(23);
				pane.getChildren().add(btnNumber1);
			}

			if (btn2) {
				btnNumber2 = new Button(btn2txt);
				btnNumber2.setOnMouseClicked(e -> {
					CreateLog();
					System.exit(1);
				});
				btnNumber2.setLayoutX(new If<Double>(btn1 ^ btn2).Then(10d).Else(224d));
				btnNumber2.setLayoutY(237);
				btnNumber2.setPrefWidth(new If<Double>(btn1 ^ btn2).Then(424d).Else(210d));
				btnNumber2.setPrefHeight(23);
				pane.getChildren().add(btnNumber2);
			}
		});
	}

	/**
	 * Happens whenever you press the Ignore or Exit buttons, creates a zip file
	 * that contains a log with the Exception.
	 */
	protected void CreateLog() {
		try {
			try (PrintWriter out = new PrintWriter("Crash.log")) {
				out.println(textArea.getText());
			}
			byte[] buffer = new byte[1024];
			DateFormat dateformat = new SimpleDateFormat("MMddyyHHmmss");
			Date date = new Date();
			FileOutputStream fos = new FileOutputStream(dateformat.format(date).toString() + "-Crash.zip");
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze = new ZipEntry("Crash.log");
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream("Crash.log");
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			in.close();
			zos.closeEntry();
			zos.close();
			new File("Crash.log").delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * All Exceptions are capture with this, this method makes the stage visible and
	 * makes the Exception visible in the textArea.
	 * 
	 * @param e The captured Exception
	 */
	public void writeError(Exception e) {
		Platform.runLater(() -> {
			popupStage.show();
			textArea.appendText("\n### BEGIN EXCEPTION ###\n");
			StringBuilder sb = new StringBuilder(e.toString());
			for (StackTraceElement ste : e.getStackTrace()) {
				sb.append("\n");
				sb.append(ste);
			}
			textArea.appendText(sb.toString());
			textArea.appendText("\n### END EXCEPTION###\n");
			textArea.selectHome(); //
			textArea.deselect();   // This is to scroll to the top
		});
	}

	/**
	 * Makes the Popup frame appear
	 */
	public void show() {
		popupStage.show();
	}
}
