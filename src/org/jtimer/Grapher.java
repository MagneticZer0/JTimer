package org.jtimer;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import com.sun.javafx.charts.Legend;
import com.sun.javafx.charts.Legend.LegendItem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class Grapher extends Application {

	String graphTitle = "JTimer";
	String xDesc = "X-axis";
	NumberAxis xAxis = new NumberAxis();
	String yDesc = "Y-axis";
	NumberAxis yAxis = new NumberAxis();
	ArrayList<Series<Number, Number>> data = new ArrayList<>();
	String dataTitle = "Uncategorized";
	static Grapher grapher = null;
	static CountDownLatch latch = new CountDownLatch(1);
	double max = Double.POSITIVE_INFINITY;
	double maxDeviations = 10;


	ScatterChart<Number, Number> scatterPlot = new ScatterChart<>(xAxis, yAxis);
	/**
	 * The main entry point for all JavaFX applications.
	 * The start method is called after the init method has returned,
	 * and after the system is ready for the application to begin running.
	 *
	 * <p>
	 * NOTE: This method is called on the JavaFX Application Thread.
	 * </p>
	 *
	 * @param stage the primary stage for this application, onto which
	 *                     the application scene can be set. The primary stage will be embedded in
	 *                     the browser if the application was launched as an applet.
	 *                     Applications may create other stages, if needed, but they will not be
	 *                     primary stages and will not be embedded in the browser.
	 */
	@Override
	public void start(Stage stage) {
		stage.setTitle("Grapher");

		scatterPlot.setTitle(graphTitle);
		data.get(0).setName(dataTitle);

		Scene scene = new Scene(scatterPlot, 800, 600);

		stage.setScene(scene);
		stage.show();
	}

	public void setGraphTitle(String graphTitle) {
		this.graphTitle = graphTitle;
	}

	public void setxDesc(String xDesc) {
		this.xDesc = xDesc;
		xAxis.setLabel(xDesc);
	}

	public void setyDesc(String yDesc) {
		this.yDesc = yDesc;
		yAxis.setLabel(yDesc);
	}

	public void addData(Number x, Number y) {
		if (y.doubleValue() < max) {
			data.get(0).getData().add(new Data<>(x, y));
		}
	}

	public void setDataTitle(String dataTitle) {
		this.dataTitle = dataTitle;
	}
	
	public void setMax(double max) {
		this.max = max;
	}
	
	public void setMaxDeviations(double maxDeviations) {
		this.maxDeviations = maxDeviations;
	}

	public static void setGrapher(Grapher grapher0) {
		grapher = grapher0;
		latch.countDown();
	}

	public Grapher() {
		data.add(new Series<Number, Number>());
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
	
	public void finish() {
		Platform.runLater(() -> {
			scatterPlot.getData().addAll(data);
			prettifyView();
			for (Node node : scatterPlot.getChildrenUnmodifiable()) {
			    if (node instanceof Legend) {
			        Legend legend = (Legend) node;
			        for (LegendItem item : legend.getItems()) {
			            for (Series<Number, Number> series : scatterPlot.getData()) {
			                if (series.getName().equals(item.getText())) {
			                    item.getSymbol().setCursor(Cursor.HAND);
			                    item.getSymbol().setOnMouseClicked(event -> {
			                        if (event.getButton() == MouseButton.PRIMARY) {
			                            for (Data<Number, Number> data : series.getData()) {
			                                if (data.getNode() != null) {
			                                    data.getNode().setVisible(!data.getNode().isVisible());
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
	
	private void prettifyView() {
		double total = 0;
		double maxX = 0;
		int points = 0;
		for(Series<Number, Number> dataPoint : scatterPlot.getData()) {
			for(Data<Number, Number> data : dataPoint.getData()) {
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
		double mean = total/points;
		total = 0;
		for(Series<Number, Number> dataPoint : scatterPlot.getData()) {
			for(Data<Number, Number> data : dataPoint.getData()) {
				if (data.getNode().isVisible()) {
					total += Math.pow(data.getYValue().doubleValue()-mean, 2);
				}
			}
		}
		double deviation = Math.sqrt(total/points);
		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(Math.round((mean-maxDeviations*deviation < 0) ? 0 : mean-maxDeviations*deviation));
		yAxis.setUpperBound(Math.round(mean+maxDeviations*deviation));
		yAxis.setTickUnit(Math.round((yAxis.getUpperBound()-yAxis.getLowerBound())/10));
		xAxis.setAutoRanging(false);
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(maxX);
		xAxis.setTickUnit(Math.round(maxX/10));
	}
}
