package org.jtimer;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;

import org.jtimer.Readability.If;
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
import javafx.scene.effect.Light.Point;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The object that graphs all the data. For all intents and purposes, this class
 * is a Singleton class and multiple instances of it should not be run.
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
	/**
	 * The pane that houses the ScatterChart {@link Grapher#scatterPlot}
	 */
	private Pane pane = new Pane();
	/**
	 * The latch that you can await
	 */
	private CountDownLatch await = new CountDownLatch(1);

	/**
	 * Starts the grapher by labeling the axes, adding key listeners, and various
	 * other things that pertain to the grapher.
	 * 
	 * @param stage the primary stage
	 */
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

		Scene scene = new Scene(pane, 800, 600);
		scatterPlot.prefHeightProperty().bind(scene.heightProperty());
		scatterPlot.prefWidthProperty().bind(scene.widthProperty());
		pane.getChildren().add(scatterPlot);

		scene.setOnKeyPressed(e -> { // This is the key listener for CTRL + S
			if (save.match(e)) {
				FileChooser chooser = new FileChooser();
				chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.png)", "*.png"));
				chooser.setTitle("Select where to save the graph...");
				chooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
				File output = chooser.showSaveDialog(stage);
				WritableImage image = scatterPlot.snapshot(new SnapshotParameters(), null);
				try {
					if (output != null) {
						ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Adds a zooming feature into the scatter plot.
	 */
	private void addZoomer() {
		Rectangle selectionVisual = new Rectangle(); // This is for visuals
		Point selection = new Point();

		scatterPlot.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.isControlDown()) {
				selection.setX(e.getSceneX());
				selection.setY(e.getSceneY());
				selectionVisual.setX(e.getSceneX());
				selectionVisual.setY(e.getSceneY());
				selectionVisual.setFill(Color.TRANSPARENT);
				selectionVisual.setStroke(Color.BLACK);
				selectionVisual.getStrokeDashArray().add(5.0);
				selectionVisual.setStrokeWidth(0.5);
				pane.getChildren().add(selectionVisual);
			} else if (e.getButton() == MouseButton.SECONDARY) {
				prettifyView();
			}
		});

		scatterPlot.setOnMouseDragged(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.isControlDown()) {
				selectionVisual.setWidth(Math.abs(e.getSceneX() - selection.getX()));
				selectionVisual.setHeight(Math.abs(e.getSceneY() - selection.getY()));
				selectionVisual.setX(Math.min(selection.getX(), e.getSceneX()));
				selectionVisual.setY(Math.min(selection.getY(), e.getSceneY()));
			}
		});

		scatterPlot.setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY && e.isControlDown()) {
				pane.getChildren().remove(selectionVisual);
				if (selectionVisual.getWidth() + selectionVisual.getHeight() > 2) { // Avoids just pressing
					selectionVisual.setWidth(0);
					selectionVisual.setHeight(0);
					double x1 = xAxis.getValueForDisplay(xAxis.sceneToLocal(selection.getX(), 0).getX()).doubleValue();
					double y1 = yAxis.getValueForDisplay(yAxis.sceneToLocal(0, selection.getY()).getY()).doubleValue();
					double x2 = xAxis.getValueForDisplay(xAxis.sceneToLocal(e.getSceneX(), 0).getX()).doubleValue();
					double y2 = yAxis.getValueForDisplay(yAxis.sceneToLocal(0, e.getSceneY()).getY()).doubleValue();
					xAxis.setLowerBound(Math.round(Math.min(x1, x2)));
					xAxis.setUpperBound(Math.round(Math.max(x1, x2)));
					xAxis.setTickUnit(Math.round((xAxis.getUpperBound() - xAxis.getLowerBound()) / 10));
					yAxis.setLowerBound(Math.round(Math.min(y1, y2)));
					yAxis.setUpperBound(Math.round(Math.max(y1, y2)));
					yAxis.setTickUnit(Math.round((yAxis.getUpperBound() - yAxis.getLowerBound()) / 10));
				}
			}
		});
	}

	/**
	 * Sets the graph title. If the time method is still running it will append a
	 * percentage to the end of the title
	 * 
	 * @param graphTitle The title to set it to
	 */
	public void setGraphTitle(String graphTitle) {
		Platform.runLater(() -> {
			this.graphTitle = graphTitle;
			scatterPlot.setTitle(graphTitle);
			if (isRunning) {
				this.graphTitle += " - ";
				scatterPlot.setTitle(scatterPlot.getTitle() + " - ");
			}
		});
	}

	/**
	 * Used to set the progress of the graph so that the user knows that something
	 * is actually happening. This is formatted to only 2 decimals places.
	 * 
	 * @param progress The progress that has been completed
	 * @param warmup   If this is triggered by the warmup
	 */
	public void setProgress(Double progress, boolean warmup) {
		Platform.runLater(() -> {
			scatterPlot.setTitle(graphTitle + new If<String>(warmup).Then(" Warmup ").Else("") + String.format("%3.2f", progress * 100) + "%");
		});
	}

	/**
	 * Sets the string for the x-axis
	 * 
	 * @param xDesc The string for the x-axis
	 */
	public void setxDesc(String xDesc) {
		Platform.runLater(() -> {
			xAxis.setLabel(xDesc);
		});
	}

	/**
	 * Sets the string for the y-axis
	 * 
	 * @param yDesc The string for the y-axis
	 */
	public void setyDesc(String yDesc) {
		Platform.runLater(() -> {
			yAxis.setLabel(yDesc);
		});
	}

	/**
	 * Sets the maximum value that the graph will graph. By default this is
	 * {@link Double#POSITIVE_INFINITY}
	 * 
	 * @param max The maximum value
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * Maximum amount of deviations the graph will show by default this is 2, so
	 * data only within 2 standard deviations will be shown.
	 * 
	 * @param maxDeviations The maximum amount of deviations
	 */
	public void setMaxDeviations(double maxDeviations) {
		this.maxDeviations = maxDeviations;
	}

	/**
	 * This sets the grapher for the Singleton grapher class
	 * 
	 * @param grapher0 The grapher to be set
	 */
	public static void setGrapher(Grapher grapher0) {
		grapher = grapher0;
		latch.countDown();
	}

	/**
	 * Sets the instance of the grapher
	 */
	public Grapher() {
		setGrapher(this);
	}

	/**
	 * Starts the grapher, this is what should be used to create a grapher instance.
	 * 
	 * @return Returns the grapher instance created.
	 */
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
	 * Awaits the grapher to finish everything
	 * @throws InterruptedException If the latch throws an exception
	 */
	public void await() throws InterruptedException {
		await.await();
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
			addZoomer();
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
												item.getSymbol().setOpacity(If(data.getNode().isVisible()).Then(1d).Else(0.25));
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
			await.countDown();
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
		yAxis.setLowerBound(Math.round(If((mean - maxDeviations * deviation < 0) || (deviation == 0)).Then(0d).Else(mean-maxDeviations*deviation)));
		yAxis.setUpperBound(Math.round(If(deviation != 0).Then(mean + maxDeviations*deviation).Else(2*mean)));
		yAxis.setTickUnit(Math.round((yAxis.getUpperBound() - yAxis.getLowerBound()) / 10));
		xAxis.setAutoRanging(false);
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(maxX);
		xAxis.setTickUnit(Math.round(maxX / 10));
	}

	/**
	 * Calculates the line of best fit using linear regression. <br>
	 * <b>CURRENTLY AN EXPERIMENTAL FEATURE</b>
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
			//series.setName(series.getName() + " " + regressions.first());
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
	
	/**
	 * Little helper function to improve code readability
	 * @param conditional The conditional to check
	 * @return An {@link If} Object
	 */
	private If<Double> If(boolean conditional) {
		return new If<Double>(conditional);
	}
}
