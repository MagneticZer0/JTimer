package org.jtimer;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;

import org.jtimer.Regression.FunctionalFit;
import org.jtimer.Regression.LinearRegression;
import org.jtimer.Regression.PolynomialFit;

import com.sun.javafx.charts.Legend;
import com.sun.javafx.charts.Legend.LegendItem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The object that graphs all the data
 * 
 * @author MagneticZero
 */
public class Grapher extends Application {

	/**
	 * Title of the graph
	 */
	private String graphTitle = "JTimer - ";
	/**
	 * Number axis for the x-axis
	 */
	private NumberAxis xAxis = new NumberAxis();
	/**
	 * Number axis for the y-axis
	 */
	private NumberAxis yAxis = new NumberAxis();
	/**
	 * The grapher object
	 */
	private static Grapher grapher = null;
	/**
	 * The latch for the grapher
	 */
	private static CountDownLatch latch = new CountDownLatch(1);
	/**
	 * The maximum Y for the graph By default this is
	 * {@link Double#POSITIVE_INFINITY}
	 */
	private double max = Double.POSITIVE_INFINITY;
	/**
	 * Max standard deviations shown in graph by default this is 2
	 */
	private double maxDeviations = 2;
	/**
	 * If the timer is still running
	 */
	private boolean isRunning = true;
	/**
	 * Keybind to save the graph By default this is CTRL + S
	 */
	private KeyCombination save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
	/**
	 * The actual scatter plot
	 */
	public ScatterChart<Number, Number> scatterPlot = new ScatterChart<>(xAxis, yAxis);

	@Override
	public void start(Stage stage) {
		xAxis.setLabel("Repetitions");
		yAxis.setLabel("Time (ns)");
		xAxis.setAnimated(false);
		yAxis.setAnimated(false);

		stage.setTitle("Grapher");
		stage.setOnCloseRequest(e -> {
			System.exit(0);
		});

		scatterPlot.setTitle(graphTitle);

		Scene scene = new Scene(scatterPlot, 800, 600);

		scene.setOnKeyPressed(e -> {
			if (save.match(e)) {
				FileChooser chooser = new FileChooser();
				chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.png)", "*.png"));
				chooser.setTitle("Select where to save the graph...");
				File output = chooser.showSaveDialog(stage);
				WritableImage image = scatterPlot.snapshot(new SnapshotParameters(), null);
				try {
					ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Sets the graph title. If the time method is still running it will append a
	 * percentage to the end of the title
	 * 
	 * @param graphTitle The title to set it to
	 */
	public void setGraphTitle(String graphTitle) {
		this.graphTitle = graphTitle;
		scatterPlot.setTitle(graphTitle);
		if (isRunning) {
			this.graphTitle += " - ";
			scatterPlot.setTitle(scatterPlot.getTitle() + " - ");
		}
	}

	/**
	 * Used to set the progress of the graph so that the user knows that something
	 * is actually happening
	 * 
	 * @param progress The progress that has been completed
	 */
	public void setProgress(Double progress) {
		Platform.runLater(() -> {
			scatterPlot.setTitle(graphTitle + String.format("%3.2f", progress * 100) + "%");
		});
	}

	/**
	 * Sets the string for the x-axis
	 * 
	 * @param xDesc The string for the x-axis
	 */
	public void setxDesc(String xDesc) {
		xAxis.setLabel(xDesc);
	}

	/**
	 * Sets the string for the y-axis
	 * 
	 * @param yDesc The string for the y-axis
	 */
	public void setyDesc(String yDesc) {
		yAxis.setLabel(yDesc);
	}

	/**
	 * Sets the maximum value that the graph will graph. By default this is
	 * {@link Double#POSITIVE_INFINITY}
	 * 
	 * @see Double#POSITIVE_INFINITY
	 * @param max The maximum value
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * Maximum amount of deviations the graph will show by default this is 3, so
	 * data only within 3 standard deviations will be shown.
	 * 
	 * @param maxDeviations The maximum amount of deviations
	 */
	public void setMaxDeviations(double maxDeviations) {
		this.maxDeviations = maxDeviations;
	}

	public static void setGrapher(Grapher grapher0) {
		grapher = grapher0;
		latch.countDown();
	}

	public Grapher() {
		setGrapher(this);
	}

	public static Grapher start() {
		new Thread() {
			@Override
			public void run() {
				Application.launch(Grapher.class);
			}
		}.start();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return grapher;
	}

	/**
	 * Finishes a graph by:
	 * <ul>
	 * <li>Adding all the data</li>
	 * <li>Calculating lines of best fit</li>
	 * <li>Limiting the graph view</li>
	 * <li>Allows toggling of data</li>
	 * </ul>
	 */
	private void finish() {
		Platform.runLater(() -> {
			isRunning = false;
			scatterPlot.setTitle(scatterPlot.getTitle().split(" - ")[0]);
			lineOfBestFit();
			prettifyView();
			for (Node node : scatterPlot.getChildrenUnmodifiable()) {
				if (node instanceof Legend) {
					Legend legend = (Legend) node;
					for (LegendItem item : legend.getItems()) {
						for (Series<Number, Number> series : scatterPlot.getData()) {
							if (series.getName().equals(item.getText())) {
								item.getSymbol().setCursor(Cursor.HAND);
								item.getSymbol().setOnMouseClicked(e -> {
									if (e.getButton() == MouseButton.PRIMARY) {
										for (Data<Number, Number> data : series.getData()) {
											if (data.getNode() != null) {
												data.getNode().setVisible(!data.getNode().isVisible());
												item.getSymbol().setOpacity(data.getNode().isVisible() ? 1 : 0.25);
											}
										}
										prettifyView();
									}
								});
								break;
							}
						}
					}
				}
			}
		});

	}

	/**
	 * "Prettifies" a graph by limiting the view of it to within
	 * {@link Grapher#maxDeviations} standard deviations Also makes the data points
	 * on the graph a bit smaller since they're too big by default.
	 */
	private void prettifyView() {
		double total = 0;
		double maxX = 0;
		int points = 0;
		for (Series<Number, Number> dataPoint : scatterPlot.getData()) {
			for (Data<Number, Number> data : dataPoint.getData()) {
				data.getNode().setScaleX(0.7);
				data.getNode().setScaleY(0.7);
				if (data.getNode().isVisible()) {
					total += data.getYValue().doubleValue();
					points++;
					if (maxX < data.getXValue().doubleValue()) {
						maxX = data.getXValue().doubleValue();
					}
				}
			}
		}
		double mean = total / points;
		total = 0;
		for (Series<Number, Number> dataPoint : scatterPlot.getData()) {
			for (Data<Number, Number> data : dataPoint.getData()) {
				if (data.getNode().isVisible()) {
					total += Math.pow(data.getYValue().doubleValue() - mean, 2);
				}
			}
		}
		double deviation = Math.sqrt(total / points);
		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(Math.round(((mean - maxDeviations * deviation < 0) || (deviation == 0)) ? 0 : mean - maxDeviations * deviation));
		yAxis.setUpperBound(Math.round((deviation != 0) ? mean + maxDeviations * deviation : 2 * mean));
		yAxis.setTickUnit(Math.round((yAxis.getUpperBound() - yAxis.getLowerBound()) / 10));
		xAxis.setAutoRanging(false);
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(maxX);
		xAxis.setTickUnit(Math.round(maxX / 10));
	}

	/**
	 * Calculates the line of best fit using linear regression. 
	 * <br><b>CURRENTLY AN EXPERIMENTAL FEATURE</b>
	 */
	private void lineOfBestFit() {
		TreeSet<LinearRegression> regressions = null;
		for (Series<Number, Number> series : scatterPlot.getData()) {
			regressions = new TreeSet<>();
			double[][] data = getData(series);
			LinearRegression[] fit = { 
					new PolynomialFit(data[0], data[1], 2).name("O(n)"),
					new PolynomialFit(data[0], data[1], 3).name("O(n^2)"),
					new PolynomialFit(data[0], data[1], 4).name("O(n^3)"),
					new FunctionalFit(data[0], data[1], x -> Math.log(x)).name("O(lg n)"),
					new FunctionalFit(data[0], data[1], x -> x * Math.log(x)).name("O(n lg n)"),
					new FunctionalFit(data[0], data[1], x -> Math.pow(2, x)).name("O(2^n)") };
			for (LinearRegression reg : fit) {
				regressions.add(reg);
			}
			regressions.first();
		}
	}

	/**
	 * Gets the XS and YS of a series, used by {@link Grapher#lineOfBestFit()}
	 * 
	 * @param series The series
	 * @return A double[][] of xs and ys
	 */
	private double[][] getData(Series<Number, Number> series) {
		double[] xs = new double[series.getData().size()];
		double[] ys = new double[xs.length];
		for (int i = 0; i < xs.length; i++) {
			xs[i] = series.getData().get(i).getXValue().doubleValue();
			ys[i] = series.getData().get(i).getYValue().doubleValue();
		}
		return new double[][] { xs, ys };
	}
}
