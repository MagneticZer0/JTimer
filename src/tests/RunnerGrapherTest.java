package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.imageio.ImageIO;

import org.jtimer.Grapher;
import org.jtimer.Runner;
import org.jtimer.Annotations.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.sun.javafx.charts.Legend;
import com.sun.javafx.charts.Legend.LegendItem;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

@Warmup(iterations = 7)
class RunnerGrapherTest {

	static int warmupStatic = 67;
	static int warmupInstance = -2;
	static int defaultRepetitions = 0;
	static final int totalIterations = 0;
	static final int randomRepetitions = 1829;
	static int customRepetitions = 0;
	static boolean beforeClass, before, time, after, afterClass;
	static boolean timeoutTestFailure;
	static boolean extraneousMethodExecuted;
	static boolean metaMetaAnnotation;
	static int metaAnnotationMeta;

	@BeforeAll
	static void setup() throws Throwable {
		new File(System.getProperty("user.home") + "/Desktop/test.png").delete(); // Make sure there's not one already there
		Runner.time("tests.RunnerGrapherTest.class");
		Runner.getGrapher().setGraphTitle("Test");
		Runner.await();
	}

	@DisplayName("Grapher - X-Axis bounds are correct")
	@Test
	void grapherTest1() {
		assertEquals(randomRepetitions, ((NumberAxis) Runner.getGrapher().plot.getXAxis()).getUpperBound(), "X-Axis has the wrong upper bound!");
	}

	@DisplayName("Grapher - Custom Graph Title")
	@Test
	void grapherTest2() throws InterruptedException {
		Thread.sleep(500);
		assertEquals("Test", Runner.getGrapher().plot.getTitle(), "Custom graph title not properly set");
	}

	@DisplayName("Grapher - X-Axis bounds are correct after toggling")
	@Test
	void grapherTest3() throws InterruptedException {
		randomRepetitionsNode(true);
		Thread.sleep(100);
		assertEquals(10, ((NumberAxis) Runner.getGrapher().plot.getXAxis()).getUpperBound(), "X-Axis has the wrong upper bound after toggling a node!");
	}

	@DisplayName("Grapher - Node visiblity after being toggled")
	@Test
	void grapherTest4() {
		assertTrue(randomRepetitionsNode(false), "Clicked node did not toggle visibility.");
	}

	@DisplayName("Grapher - Saving graphs")
	@Test
	void grapherTest5() throws InterruptedException, IOException {
		saveKeypress();
		typeKeys(KeyEvent.VK_T, KeyEvent.VK_E, KeyEvent.VK_S, KeyEvent.VK_T, KeyEvent.VK_ENTER);
		Thread.sleep(1000);
		BufferedImage savedImage = ImageIO.read(new File(System.getProperty("user.home") + "/Desktop/test.png"));
		assertAll("Saving image went wrong!", () -> assertAll("Image dimensions are incorrect!", () -> assertEquals(800, savedImage.getWidth(), "Image width is incorrect"), () -> assertEquals(600, savedImage.getHeight(), "Image height is incorrect")), () -> assertTrue(new File(System.getProperty("user.home") + "/Desktop/test.png").delete(), "Graph save file didn't exist!"));
	}

	@DisplayName("Grapher - Zooming into a graph")
	@RepeatedTest(5)
	void grapherTest6() throws InterruptedException {
		double[] previousBounds = getBounds();
		zoomRobot();
		Thread.sleep(500);
		double[] newBounds = getBounds();
		unzoomRobot();
		Thread.sleep(500);
		double[] newOldBounds = getBounds();
		assertAll("Zoom didn't properly work", () -> assertAll("Zoom in didn't properly work", () -> assertNotEquals(previousBounds[0], newBounds[0]), () -> assertNotEquals(previousBounds[1], newBounds[1]), () -> assertNotEquals(previousBounds[2], newBounds[2]), () -> assertNotEquals(previousBounds[3], newBounds[3])), () -> assertAll("Zoom out didn't properly work", () -> assertEquals(previousBounds[0], newOldBounds[0]), () -> assertEquals(previousBounds[1], newOldBounds[1]), () -> assertEquals(previousBounds[2], newOldBounds[2]), () -> assertEquals(previousBounds[3], newOldBounds[3])));
	}

	@DisplayName("Grapher - X-Axis bounds are correct after retoggling")
	@Test
	void grapherTest7() throws InterruptedException {
		randomRepetitionsNode(true);
		Thread.sleep(100);
		assertEquals(randomRepetitions, ((NumberAxis) Runner.getGrapher().plot.getXAxis()).getUpperBound(), "X-Axis has the wrong upper bound after retoggling a node!");
	}

	@DisplayName("Grapher - Node visiblity after being retoggled")
	@Test
	void grapherTest8() {
		assertFalse(randomRepetitionsNode(false), "Clicked node did not retoggle visibility.");
	}

	@DisplayName("Grapher - Custom Names")
	@Test
	void grapherTest9() {
		boolean customName = false;
		for (Series<Number, Number> data : Runner.getGrapher().plot.getData()) {
			if (data.getName().contains("Pickle")) {
				customName = true;
			}
		}
		assertTrue(customName, "Custom name was not set in grapher");
	}

	@DisplayName("Grapher - Best fit calculation")
	@Test
	void grapherTest10() throws Throwable {
		Field graph = Grapher.class.getDeclaredField("bestFitPlot");
		graph.setAccessible(true);

		assertTrue(graph.get(Runner.getGrapher()) == null, "Best fit chart was created!");
	}
	
	@DisplayName("Grapher - Theme")
	@Test
	void grapherTest11() throws Throwable {
		Runner.getGrapher().setTheme(Color.BLUE);
		
		Field pane = Grapher.class.getDeclaredField("pane");
		pane.setAccessible(true);
		Pane grapherPane = (Pane) pane.get(Runner.getGrapher());
		Thread.sleep(100);
		
		assertEquals(Color.BLUE, grapherPane.getBackground().getFills().get(0).getFill(), "Pane color not correctly set!");
	}

	@DisplayName("Runner - Default repetitions")
	@Test
	void runnerTest1() {
		assertEquals(10, defaultRepetitions, "Default repetitions is supposed to be 10, but was " + defaultRepetitions);
	}

	@DisplayName("Runner custom repetitions")
	@Test
	void runnerTest2() {
		assertEquals(randomRepetitions, customRepetitions, "Custom repetitions is supposed to be " + randomRepetitions + ", but was " + customRepetitions);
	}

	@DisplayName("Runner - Execution order")
	@Test
	void runnerTest3() {
		assertTrue(beforeClass && before && time && after && afterClass, "Order of execution");
	}

	@DisplayName("Runner - Test Timeout")
	@Test
	void runnerTest4() {
		assertFalse(timeoutTestFailure, "Timeout didn't properly work");
	}

	@DisplayName("Runner - Unused Methods")
	@Test
	void runnerTest5() {
		assertFalse(extraneousMethodExecuted, "A non-annotated method was executed by the runner!");
	}

	@DisplayName("Runner - Warmup Ran")
	@Test
	void runnerTest6() throws Throwable {
		Field totalIterationsField = RunnerGrapherTest.class.getDeclaredField("totalIterations");
		totalIterationsField.setAccessible(true);

		assertEquals(17, totalIterationsField.get(null), "Warmup did not run");
	}

	@DisplayName("Runner - Warmup Cleanup")
	@Test
	void runnerTest7() {
		assertAll("Warmup didn't properly reset variables", () -> assertEquals(77, warmupStatic, "Static variables were modified improperly!"), () -> assertEquals(8, warmupInstance, "Instance variables were modified improperly!"));
	}

	@DisplayName("Runner - Meta annotation handler")
	@Test
	void runnerTest8() {
		boolean metaAnnotation = false;
		for (Series<Number, Number> data : Runner.getGrapher().plot.getData()) {
			if (data.getName().contains("META")) {
				metaAnnotation = true;
			}
		}
		assertTrue(metaAnnotation, "Meta annotations not properly handeled");
	}

	@DisplayName("Runner - Double meta annotation handler")
	@Test
	void runnerTest9() {
		assertAll("Double meta annotation order is weird", () -> assertFalse(metaMetaAnnotation, "Double meta annotations not properly handeled"), () -> assertEquals(2, metaAnnotationMeta, "Double meta annotations not properly handeled"));
	}

	@AfterAll
	static void tearDown() {
		new File(System.getProperty("user.home") + "/Desktop/test.png").delete(); // Make sure nothing is left over
		Runner.getGrapher().clearData();
	}

	// This is to test the order of execution
	@BeforeClass
	void beforeClassTest() {
		beforeClass = true;
	}

	@Before
	void beforeTest() {
		if (beforeClass) {
			before = true;
		}
	}

	@Time
	void timeTest() {
		if (before) {
			time = true;
		}
	}

	@After
	void afterTest() {
		if (time) {
			after = true;
		}
	}

	@AfterClass
	void afterClassTest() {
		if (after) {
			afterClass = true;
		}
	}
	//

	// This is to test repetition count
	@Time
	void defaultRepetitionsTest() {
		defaultRepetitions++;
	}

	@Time(repeat = randomRepetitions)
	void randomRepetitionsTest() {
		customRepetitions++;
	}
	//

	// This is to test timeout
	@Time(timeout = 1000) // Remember, this is in milliseconds
	void timeoutTest() throws InterruptedException {
		Thread.sleep(1000);
		timeoutTestFailure = true;
	}
	//

	// This is to test extraneous methods that should not be run
	void whoCares() {
		extraneousMethodExecuted = true;
	}
	//

	// This is used to test warmup
	@Time
	void warmupTest() throws Throwable {
		Field totalIterationsField = RunnerGrapherTest.class.getDeclaredField("totalIterations");
		totalIterationsField.setAccessible(true);

		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(totalIterationsField, totalIterationsField.getModifiers() & ~Modifier.FINAL);

		totalIterationsField.set(null, totalIterationsField.getInt(null) + 1);

		warmupStatic++;
		warmupInstance++;
	}

	@org.jtimer.Annotations.DisplayName("Pickle")
	@Time
	void displayName() {

	}

	@MetaAnnotationNB
	void metaAnnotation() {

	}

	@MetaMetaAnnotationNB
	void metaMetaAnnotation() {
		metaMetaAnnotation = true;
	}

	@MetaAnnotationMetaNB
	void metaAnnotationMeta() {
		metaAnnotationMeta++;
	}

	// Helper things that use a robot
	private void saveKeypress() {
		try {
			Robot saver = new Robot();
			Platform.runLater(() -> Runner.getGrapher().plot.requestFocus());
			saver.keyPress(KeyEvent.VK_CONTROL);
			saver.keyPress(KeyEvent.VK_S);
			saver.keyRelease(KeyEvent.VK_CONTROL);
			saver.keyRelease(KeyEvent.VK_S);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private void typeKeys(int... keys) {
		try {
			Robot typer = new Robot();
			for (int key : keys) {
				Platform.runLater(() -> Runner.getGrapher().plot.requestFocus());
				typer.keyPress(key);
				typer.keyRelease(key);
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private void press(int[] coords) {
		try {
			Robot clicker = new Robot();
			Platform.runLater(() -> Runner.getGrapher().plot.requestFocus());
			clicker.mouseMove(coords[0], coords[1]);
			clicker.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			clicker.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private void zoomRobot() {
		try {
			Robot zoomer = new Robot();
			Platform.runLater(() -> Runner.getGrapher().plot.requestFocus());
			zoomer.mouseMove((int) Runner.getGrapher().plot.getScene().getWindow().getX() + 100, (int) Runner.getGrapher().plot.getScene().getWindow().getY() + 100);
			zoomer.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			zoomer.keyPress(KeyEvent.VK_CONTROL);
			zoomer.mouseMove((int) Runner.getGrapher().plot.getScene().getWindow().getX() + 200, (int) Runner.getGrapher().plot.getScene().getWindow().getY() + 200);
			zoomer.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			zoomer.keyRelease(KeyEvent.VK_CONTROL);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private void unzoomRobot() {
		try {
			Robot unzoomer = new Robot();
			Platform.runLater(() -> Runner.getGrapher().plot.requestFocus());
			unzoomer.mousePress(InputEvent.BUTTON3_DOWN_MASK);
			unzoomer.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	//

	private boolean randomRepetitionsNode(boolean click) {
		int[] coords = new int[2];
		for (Node node : Runner.getGrapher().plot.getChildrenUnmodifiable()) {
			if (node instanceof Legend) {
				for (LegendItem item : ((Legend) node).getItems()) {
					if (item.getText().contains("RandomRepetitionsTest")) {
						Node n = item.getSymbol();
						coords[0] = (int) (n.getScene().getWindow().getX() + n.localToScene(n.getBoundsInLocal()).getMinX() + 12);
						coords[1] = (int) (n.getScene().getWindow().getY() + n.localToScene(n.getBoundsInLocal()).getMinY() + 35);
						if (click) {
							press(coords);
						}
						return n.getOpacity() != 1;
					}
				}
			}
		}
		return false;
	}

	private double[] getBounds() {
		double[] bounds = new double[4];
		bounds[0] = ((NumberAxis) Runner.getGrapher().plot.getXAxis()).getLowerBound();
		bounds[1] = ((NumberAxis) Runner.getGrapher().plot.getXAxis()).getUpperBound();
		bounds[2] = ((NumberAxis) Runner.getGrapher().plot.getYAxis()).getLowerBound();
		bounds[3] = ((NumberAxis) Runner.getGrapher().plot.getYAxis()).getUpperBound();
		return bounds;
	}
}

@Time(repeat = 7)
@org.jtimer.Annotations.DisplayName("META")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@interface MetaAnnotationNB {

}

@Time(repeat = 0)
@org.jtimer.Annotations.DisplayName("METAMETA")
@MetaAnnotationNB
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@interface MetaMetaAnnotationNB {

}

@MetaAnnotationNB
@Time(repeat = 2)
@org.jtimer.Annotations.DisplayName("META META")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@interface MetaAnnotationMetaNB {

}
