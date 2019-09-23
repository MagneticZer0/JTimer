/*
 * JTimer is a Java library that contains various methods and annotations that allow's one to 
 * time various methods and output it into a graph.
 * Copyright (C) 2019  Harley Merkaj
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://github.com/MagneticZer0/JTimer/blob/master/LICENSE> 
 * or <https://www.gnu.org/licenses/>.
 */
package org.jtimer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;

import org.jtimer.Collections.MultiMap;
import org.jtimer.Readability.If;
import org.jtimer.Regression.FunctionalFit;
import org.jtimer.Regression.PolynomialFit;
import org.jtimer.Regression.Regression;

import com.sun.javafx.charts.Legend;
import com.sun.javafx.charts.Legend.LegendItem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Axis;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The object that graphs all the data. For all intents and purposes, this class
 * is a Singleton class and multiple instances of it should not be run. As the
 * behavior may not be as expected.
 * 
 * @author MagneticZero
 */
public class Grapher extends Application {

	/**
	 * This is the title of the graph.
	 */
	private String graphTitle = "JTimer - ";
	/**
	 * This is the {@link javafx.scene.chart.NumberAxis Number Axis} for the x-axis
	 * of the main graph {@link org.jtimer.Grapher#plot plot}.
	 */
	private NumberAxis xAxis = new NumberAxis();
	/**
	 * This is the {@link javafx.scene.chart.NumberAxis Number Axis} for the y-axis
	 * of the main graph {@link org.jtimer.Grapher#plot plot}.
	 */
	private NumberAxis yAxis = new NumberAxis();
	/**
	 * This is the {@link org.jtimer.Grapher grapher} object created.
	 */
	private static Grapher grapher = null;
	/**
	 * This {@link java.util.concurrent.CountDownLatch latch} is used in the
	 * creation of the {@link org.jtimer.Grapher graph}.
	 */
	private static CountDownLatch latch = new CountDownLatch(1);
	/**
	 * The maximum Y for the {@link org.jtimer.Grapher graph}. By default this is
	 * {@link java.lang.Double#POSITIVE_INFINITY infinity}.
	 */
	@SuppressWarnings("unused") // Used through reflection
	private double max = Double.POSITIVE_INFINITY;
	/**
	 * Max standard deviations shown in {@link org.jtimer.Grapher graph} by default
	 * this is 2. This, theoretically, means that around 95% of data collected will
	 * be shown.
	 */
	private double maxDeviations = 2;
	/**
	 * If the {@link org.jtimer.Runner} is still going.
	 */
	private boolean isRunning = true;
	/**
	 * Keybind to save the {@link org.jtimer.Grapher graph}.
	 * <br>
	 * By default this is CTRL + S.
	 */
	private KeyCombination save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
	/**
	 * The main {@link javafx.scene.chart.ScatterChart scatter chart}, this is where
	 * all timed data will go.
	 */
	private ScatterChart<Number, Number> plot = new ScatterChart<>(xAxis, yAxis);
	/**
	 * The {@link javafx.scene.chart.ScatterChart scatter chart} that will be used
	 * for the {@link org.jtimer.Grapher#lineOfBestFit() line of best fit}.
	 */
	private ScatterChart<Number, Number> bestFitPlot = null;
	/**
	 * The {@link javafx.scene.layout.Pane pane} that houses the
	 * {@link org.jtimer.Grapher#plot ScatterChart} and in the case where a
	 * {@link org.jtimer.Grapher#lineOfBestFit() line of best fit} is calculated it
	 * is shared with the {@link org.jtimer.Grapher#bestFitPlot best fit plot}.
	 */
	private Pane pane = new Pane();
	/**
	 * This is a {@link java.util.concurrent.CountDownLatch latch} for the
	 * {@link org.jtimer.Grapher grapher} so that the {@link org.jtimer.Runner
	 * runner} can {@link java.util.concurrent.CountDownLatch#await() await} it.
	 * Theoretically, this should be released when the {@link org.jtimer.Grapher
	 * grapher} has finished everything.
	 */
	private CountDownLatch await = new CountDownLatch(1);
	/**
	 * Sotres the color for the theme so that the color can be applied to the best
	 * fit graph if it is applied too early.
	 */
	private Color themeColor = null;

	/**
	 * Starts the {@link org.jtimer.Grapher grapher} by setting up the essential
	 * components of the graph and sets some default values.
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
		stage.setOnCloseRequest(e -> System.exit(0));

		plot.setTitle(graphTitle);

		Scene scene = new Scene(pane, 800, 600);
		plot.prefHeightProperty().bind(pane.heightProperty());
		plot.prefWidthProperty().bind(pane.widthProperty());
		pane.getChildren().add(plot);

		scene.setOnKeyPressed(e -> { // This is the key listener for CTRL + S
			if (save.match(e)) {
				FileChooser chooser = new FileChooser();
				chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image (*.png)", "*.png"));
				chooser.setTitle("Select where to save the graph...");
				chooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
				File output = chooser.showSaveDialog(stage);
				WritableImage image = pane.snapshot(new SnapshotParameters(), null);
				try {
					if (output != null) {
						ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output);
					}
				} catch (IOException ex) {
					Runner.exceptionCatcher.writeError(ex);
				}
			}
		});

		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Adds a zooming feature into the {@link javafx.scene.chart.ScatterChart
	 * scatter chart}. By default, the Control key must be pressed in order to zoom
	 * into a graph.
	 */
	@SuppressWarnings("unchecked")
	private void addZoomer() {
		for (ScatterChart<Number, Number> chart : new ScatterChart[] { plot, bestFitPlot }) {
			if (chart != null) {
				Rectangle selectionVisual = new Rectangle(); // This is for visuals
				Point selection = new Point();

				chart.setOnMousePressed(e -> {
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
						prettifyView(chart);
					}
				});

				chart.setOnMouseDragged(e -> {
					if (e.getButton() == MouseButton.PRIMARY && e.isControlDown()) {
						selectionVisual.setWidth(Math.abs(e.getSceneX() - selection.getX()));
						selectionVisual.setHeight(Math.abs(e.getSceneY() - selection.getY()));
						selectionVisual.setX(Math.min(selection.getX(), e.getSceneX()));
						selectionVisual.setY(Math.min(selection.getY(), e.getSceneY()));
					}
				});

				chart.setOnMouseReleased(e -> {
					if (e.getButton() == MouseButton.PRIMARY && e.isControlDown()) {
						pane.getChildren().remove(selectionVisual);
						if (selectionVisual.getWidth() + selectionVisual.getHeight() > 2) { // Avoids just pressing
							selectionVisual.setWidth(0);
							selectionVisual.setHeight(0);
							NumberAxis axisX = (NumberAxis) chart.getXAxis();
							NumberAxis axisY = (NumberAxis) chart.getYAxis();
							double x1 = axisX.getValueForDisplay(axisX.sceneToLocal(selection.getX(), 0).getX()).doubleValue();
							double y1 = axisY.getValueForDisplay(axisY.sceneToLocal(0, selection.getY()).getY()).doubleValue();
							double x2 = axisX.getValueForDisplay(axisX.sceneToLocal(e.getSceneX(), 0).getX()).doubleValue();
							double y2 = axisY.getValueForDisplay(axisY.sceneToLocal(0, e.getSceneY()).getY()).doubleValue();
							axisX.setLowerBound(Math.round(Math.min(x1, x2)));
							axisX.setUpperBound(Math.round(Math.max(x1, x2)));
							axisX.setTickUnit(Math.round((axisX.getUpperBound() - axisX.getLowerBound()) / 10));
							axisY.setLowerBound(Math.round(Math.min(y1, y2)));
							axisY.setUpperBound(Math.round(Math.max(y1, y2)));
							axisY.setTickUnit(Math.round((axisY.getUpperBound() - axisY.getLowerBound()) / 10));
						}
					}
				});
			}
		}
	}

	/**
	 * Sets the {@link org.jtimer.Grapher grapher} title. If the
	 * {@link org.jtimer.Runner#time(String, org.jtimer.Runner.TimeMethod) time} method
	 * is still running it will append a percentage to the end of the title. If the
	 * {@link org.jtimer.Runner#warmup(Object, org.jtimer.Runner.TimeMethod) warmup} is
	 * still running, it will append a warmup tag and a percentage to the end of the
	 * title.
	 * 
	 * @param graphTitle The title to set it to
	 */
	public void setGraphTitle(String graphTitle) {
		Platform.runLater(() -> {
			this.graphTitle = graphTitle;
			plot.setTitle(graphTitle);
			if (isRunning) {
				this.graphTitle += " - ";
				plot.setTitle(plot.getTitle() + " - ");
			}
		});
	}

	/**
	 * Used to set the progress of the {@link org.jtimer.Grapher graph} so that the
	 * user knows that something is actually happening. This is formatted to only 2
	 * decimals places.
	 * 
	 * @param progress The progress that has been completed
	 * @param warmup   If this is triggered by the
	 *                 {@link org.jtimer.Runner#warmup(Object, org.jtimer.Runner.TimeMethod)
	 *                 warmup}
	 */
	public void setProgress(Double progress, boolean warmup) {
		Platform.runLater(() -> plot.setTitle(graphTitle + new If<String>(warmup).Then(" Warmup ").Else("") + String.format("%3.2f", progress * 100) + "%"));
	}

	/**
	 * Sets the label for the {@link org.jtimer.Grapher#xAxis x-axis}, by default
	 * this is "Repetitions."
	 * 
	 * @param xDesc The string for the {@link org.jtimer.Grapher#xAxis x-axis} label
	 */
	public void setxDesc(String xDesc) {
		Platform.runLater(() -> xAxis.setLabel(xDesc));
	}

	/**
	 * Sets the label for the {@link org.jtimer.Grapher#xAxis y-axis}, by default
	 * this is "Time (ns)."
	 * 
	 * @param yDesc The string for the {@link org.jtimer.Grapher#xAxis y-axis} label
	 */
	public void setyDesc(String yDesc) {
		Platform.runLater(() -> yAxis.setLabel(yDesc));
	}

	/**
	 * Sets the {@link org.jtimer.Grapher#max maximum} value that the
	 * {@link org.jtimer.Grapher graph} will graph.
	 * <br>
	 * By default this is {@link java.lang.Double#POSITIVE_INFINITY infinity}, I.E.
	 * there is no maximum value that the graph will not allow.
	 * 
	 * @param max The maximum value
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * Maximum amount of {@link org.jtimer.Grapher#maxDeviations deviations} the
	 * {@link org.jtimer.Grapher graph} will show.
	 * <br>
	 * By default this is 2, so data only within 2 standard deviations will be
	 * shown. Statisitcally, this means around 95% of the data collected will be
	 * shown.
	 * 
	 * @param maxDeviations The maximum amount of deviations
	 */
	public void setMaxDeviations(double maxDeviations) {
		this.maxDeviations = maxDeviations;
	}

	/**
	 * This sets the grapher for the Singleton {@link org.jtimer.Grapher grapher}
	 * class.
	 * 
	 * @param grapher0 The {@link org.jtimer.Grapher grapher} to be set
	 */
	public static void setGrapher(Grapher grapher0) {
		grapher = grapher0;
		latch.countDown();
	}

	/**
	 * Sets the instance of the {@link org.jtimer.Grapher grapher}.
	 */
	public Grapher() {
		setGrapher(this);
	}

	/**
	 * Starts the {@link org.jtimer.Grapher grapher}, this is what should be used to
	 * create a {@link org.jtimer.Grapher grapher} instance.
	 * 
	 * @return Returns the {@link org.jtimer.Grapher grapher} instance created.
	 */
	public static Grapher start() {
		new Thread(() -> Application.launch(Grapher.class)).start();
		try {
			latch.await();
		} catch (InterruptedException e) {
			Runner.exceptionCatcher.writeError(e);
		}
		return grapher;
	}

	/**
	 * Awaits the {@link org.jtimer.Grapher grapher} to finish everything.
	 * 
	 * @throws InterruptedException If the {@link org.jtimer.Grapher#latch latch}
	 *                              throws an exception
	 */
	public void await() throws InterruptedException {
		await.await();
	}

	/**
	 * Sets the theme of the {@link org.jtimer.Grapher graph}. What this will do is
	 * set the background {@link javafx.scene.paint.Color color} of the
	 * {@link org.jtimer.Grapher graph} as will as change the text
	 * {@link javafx.scene.paint.Color color} of the {@link org.jtimer.Grapher
	 * graph} to make sure that the text is readable.
	 * 
	 * @param color The {@link javafx.scene.paint.Color color} to set the theme
	 *              around.
	 */
	public void setTheme(Color color) {
		Platform.runLater(() -> {
			themeColor = color;
			double perBri = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
			pane.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
			if (perBri > 0.5) {
				setTextColor(Color.BLACK);
			} else {
				setTextColor(Color.WHITE);
			}
			for (ScatterChart<?, ?> chart : new ScatterChart[] { plot, bestFitPlot }) {
				if (chart != null) {
					if (!color.darker().darker().equals(color)) {
						chart.lookup(".chart-legend").setStyle("-fx-background-color: " + color.darker().darker().toString().replace("0x", "#") + ";");
					} else {
						chart.lookup(".chart-legend").setStyle("-fx-background-color: " + color.brighter().brighter().toString().replace("0x", "#") + ";");
					}
				}
			}
		});
	}

	/**
	 * Sets the text {@link javafx.scene.paint.Color color} of all text components
	 * on the {@link org.jtimer.Grapher graph} as well as any tick marks and
	 * {@link org.jtimer.Grapher graph} lines.
	 * 
	 * @param color The {@link javafx.scene.paint.Color color} to set the text to
	 */
	public void setTextColor(Color color) {
		Platform.runLater(() -> {
			for (ScatterChart<?, ?> chart : new ScatterChart[] { plot, bestFitPlot }) {
				if (chart != null) {
					String hexValue = color.toString().replace("0x", "#");
					for (Axis<?> axis : new Axis[] { chart.getXAxis(), chart.getYAxis() }) {
						axis.setTickLabelFill(color);
						axis.lookup(".axis-tick-mark").setStyle("-fx-stroke: " + hexValue + ";");
						if (!color.darker().darker().equals(color)) {
							axis.lookup(".axis-minor-tick-mark").setStyle("-fx-stroke: " + color.darker().darker().toString().replace("0x", "#") + ";");
						} else {
							axis.lookup(".axis-tick-mark").setStyle("-fx-stroke: " + color.darker().darker().toString().replace("0x", "#") + ";");
							axis.lookup(".axis-minor-tick-mark").setStyle("-fx-stroke: " + hexValue + ";");
						}
					}
					chart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent ;");
					chart.lookup(".chart-title").setStyle("-fx-text-fill: " + hexValue + ";");
					chart.lookup(".chart-vertical-grid-lines").setStyle("-fx-stroke: " + color.desaturate().toString().replace("0x", "#") + ";");
					chart.lookup(".chart-horizontal-grid-lines").setStyle("-fx-stroke: " + color.desaturate().toString().replace("0x", "#") + ";");
					for (Node axis : chart.lookupAll(".axis-label")) {
						axis.setStyle("-fx-text-fill: " + hexValue + ";");
					}
					for (Node legendItem : chart.lookupAll(".chart-legend-item")) {
						legendItem.setStyle("-fx-text-fill: " + hexValue + ";");
					}
				}
			}
		});
	}

	/**
	 * This method is used to clear all the data within the graphs. There's really
	 * no reason for you to use this method, I use it only for clearing data between
	 * tests.
	 */
	public void clearData() {
		Platform.runLater(() -> {
			plot.getData().clear();
			if (bestFitPlot != null) {
				bestFitPlot.getData().clear();
			}
		});
	}

	/**
	 * Returns the plots used by the Grapher. If the best fit plot is not being used
	 * it will not be returned in the list. The list is also immutable.
	 * 
	 * @return A list of plots used by the Grapher
	 */
	public List<ScatterChart<Number, Number>> getPlots() {
		ArrayList<ScatterChart<Number, Number>> plots = new ArrayList<>();
		plots.add(plot);
		if (bestFitPlot != null) {
			plots.add(bestFitPlot);
		}
		return Collections.unmodifiableList(plots);
	}

	/**
	 * Finishes a {@link org.jtimer.Grapher graph} by:
	 * <ul>
	 * <li>Adding all the data</li>
	 * <li>Calculating {@link org.jtimer.Grapher#lineOfBestFit() lines of best
	 * fit}</li>
	 * <li>{@link org.jtimer.Grapher#prettifyView(ScatterChart) Limiting} the graph
	 * view</li>
	 * <li>Allows toggling of data</li>
	 * </ul>
	 * 
	 * @param bestFit Whether or not the function of
	 *                {@link org.jtimer.Grapher#lineOfBestFit() best fit} should be
	 *                calculated
	 */
	@SuppressWarnings({ "unused", "unchecked" }) // Used through reflection
	private void finish(boolean bestFit) {
		Platform.runLater(() -> {
			MultiMap<String, LegendItem> legendMap = new MultiMap<>();
			isRunning = false;
			plot.setTitle(plot.getTitle().split(" - ")[0]);
			if (bestFit) {
				lineOfBestFit();
				if (themeColor != null) {
					setTheme(themeColor);
				}
			}
			prettifyView();
			addZoomer();
			for (ScatterChart<Number, Number> chart : new ScatterChart[] { plot, bestFitPlot }) {
				if (chart != null) {
					for (Node node : chart.getChildrenUnmodifiable()) {
						if (node instanceof Legend) {
							Legend legend = (Legend) node;
							for (LegendItem item : legend.getItems()) {
								legendMap.put(item.getText(), item);
								for (Series<Number, Number> series : chart.getData()) {
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
												prettifyView(chart);
											}
											if (e.isShiftDown()) {
												for (LegendItem otherNode : legendMap.get(item.getText())) {
													if (otherNode != item) {
														Event.fireEvent(otherNode.getSymbol(), new MouseEvent(MouseEvent.MOUSE_CLICKED, otherNode.getSymbol().getLayoutX(), otherNode.getSymbol().getLayoutY(), otherNode.getSymbol().getScaleX(), otherNode.getSymbol().getScaleY(), MouseButton.PRIMARY, 1, false, false, false, false, false, true, false, false, true, true, null));
													}
												}
											}
										});
										break;
									}
								}
							}
						}
					}
				}
			}
			await.countDown();
		});

	}

	/**
	 * "Prettifies" all {@link org.jtimer.Grapher graphs} by limiting the view of it
	 * to within {@link org.jtimer.Grapher#maxDeviations the max deviations setting}
	 * standard deviations. Also makes the data points on the
	 * {@link org.jtimer.Grapher graphs} a bit smaller since they're too big by
	 * default.
	 * 
	 * @see org.jtimer.Grapher#prettifyView(ScatterChart)
	 */
	@SuppressWarnings("unchecked")
	private void prettifyView() {
		for (ScatterChart<Number, Number> chart : new ScatterChart[] { plot, bestFitPlot }) {
			prettifyView(chart);
		}
	}

	/**
	 * "Prettifies" a {@link org.jtimer.Grapher graph} by limiting the view of it to
	 * within {@link org.jtimer.Grapher#maxDeviations the max deviations setting}
	 * standard deviations. Also makes the data points on the
	 * {@link org.jtimer.Grapher graph} a bit smaller since they're too big by
	 * default.
	 * 
	 * @param graph the graph to "prettify"
	 */
	private void prettifyView(ScatterChart<Number, Number> graph) {
		if (graph != null) {
			double total = 0;
			double maxX = 0;
			int points = 0;
			for (Series<Number, Number> dataPoint : graph.getData()) {
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
			for (Series<Number, Number> dataPoint : graph.getData()) {
				for (Data<Number, Number> data : dataPoint.getData()) {
					if (data.getNode().isVisible()) {
						total += Math.pow(data.getYValue().doubleValue() - mean, 2);
					}
				}
			}
			double deviation = Math.sqrt(total / points);
			NumberAxis axisX = (NumberAxis) graph.getXAxis();
			NumberAxis axisY = (NumberAxis) graph.getYAxis();
			axisY.setAutoRanging(false);
			axisY.setLowerBound(Math.round(If((mean - maxDeviations * deviation < 0) || (deviation == 0)).Then(0d).Else(mean - maxDeviations * deviation)));
			axisY.setUpperBound(Math.round(If(deviation != 0).Then(mean + maxDeviations * deviation).Else(2 * mean)));
			axisY.setTickUnit(Math.round((axisY.getUpperBound() - axisY.getLowerBound()) / 10));
			axisX.setAutoRanging(false);
			axisX.setLowerBound(0);
			axisX.setUpperBound(maxX);
			axisX.setTickUnit(Math.round(maxX / 10));
		}
	}

	/**
	 * Calculates the line of best fit using regression through
	 * {@link org.jtimer.Regression.Matrix matrices}.
	 * <br>
	 * <b>CURRENTLY AN EXPERIMENTAL FEATURE</b>
	 */
	private void lineOfBestFit() {
		NumberAxis bestFitXAxis = new NumberAxis();
		NumberAxis bestFitYAxis = new NumberAxis();
		bestFitXAxis.labelProperty().bind(xAxis.labelProperty());
		bestFitYAxis.labelProperty().bind(yAxis.labelProperty());
		bestFitPlot = new ScatterChart<>(bestFitXAxis, bestFitYAxis);
		TreeSet<Regression> regressions = null;
		for (Series<Number, Number> series : plot.getData()) {
			double[][] data = getData(series);
			regressions = new TreeSet<>(Arrays.asList(
				new PolynomialFit(data[0], data[1], 2).name("O(n)"),
				new PolynomialFit(data[0], data[1], 3).name("O(n^2)"),
				new PolynomialFit(data[0], data[1], 4).name("O(n^3)"),
				new FunctionalFit(data[0], data[1], Math::log).name("O(lg n)"),
				new FunctionalFit(data[0], data[1], x -> x * Math.log(x)).name("O(n lg n)"),
				new FunctionalFit(data[0], data[1], x -> Math.pow(2, x)).name("O(2^n)")
			));
			Series<Number, Number> dataSeries = new Series<>();
			dataSeries.setName(series.getName());
			for (int i = 0; i <= data[0].length; i++) {
				double fx = regressions.first().calculate(i);
				dataSeries.getData().add(new Data<Number, Number>(i, If(fx < 0 || !Double.isFinite(fx)).Then(0d).Else(fx)));
			}
			bestFitPlot.getData().add(dataSeries);
		}
		pane.getChildren().add(bestFitPlot);
		plot.prefHeightProperty().bind(pane.heightProperty().divide(2));
		bestFitPlot.prefWidthProperty().bind(pane.widthProperty());
		bestFitPlot.prefHeightProperty().bind(pane.heightProperty().divide(2));
		bestFitPlot.layoutYProperty().bind(pane.heightProperty().divide(2));
	}

	/**
	 * Gets the X's and Y's of a {@link javafx.scene.chart.XYChart.Series series}.
	 * this is used by {@link org.jtimer.Grapher#lineOfBestFit() lineOfBestFit()}
	 * 
	 * @param series The series to get the data of.
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
	 * 
	 * @param conditional The conditional to check
	 * @return An {@link org.jtimer.Readability.If If} object
	 */
	private If<Double> If(boolean conditional) {
		return new If<>(conditional);
	}
}
