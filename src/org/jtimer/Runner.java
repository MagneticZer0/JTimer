package org.jtimer;

import org.jtimer.Annotations.*;

import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * The brains behind everything I'd say. This is what times methods,
 * adds timeout to methods, adds the data to a graph, recursively finds
 * all methods inside a package, etc. It does a lot and I should probably
 * split things up a bit just to make it look better.
 */
public class Runner {

	private static Object object; // The test class that is being run
	private static Thread time = new Thread(); // The thread use to track timeout
	private static long timer = -1l; // The timer to track timeout

	/**
	 * Runs all @Time methods inside of the package
	 * pkg.
	 * @param pkg The package containing the methods to run
	 * @throws Throwable Anything thrown from running the methods.
	 */
	public static void time(String pkg) throws Throwable {
		time(pkg, new TimeMethod() {

			@Override
			public long timeMethod() {
				return System.nanoTime();
			}

			@Override
			public long convertNano(long nano) {
				return nano;
			}
			
		});
	}

	/**
	 * This will execute all methods found in the timing package
	 * The order of operations is anything with a @BeforeClass which is only once
	 * Then @Before is executed before @Time, just like JUnit
	 * @Time will execute followed by @After. Lastly @AfterClass is executed after all @Time has been executed
	 *
	 * @param pkg The package name that contains the things you want to time
	 * @throws Throwable
	 */
	public static void time(String pkg, TimeMethod timeMethod) throws Throwable {
		for(Class<?> cls : getClasses(pkg)) {
			object = cls.newInstance();
			if (!cls.isAnnotation() && !cls.isInterface() && !cls.isEnum()) {
				List<Method> beforeClass = new LinkedList<>();
				List<Method> before = new LinkedList<>();
				List<Method> afterClass = new LinkedList<>();
				List<Method> after = new LinkedList<>();
				long repetitions = 0;
				for(Method method : cls.getDeclaredMethods()) {
					if (method.isAnnotationPresent(BeforeClass.class)) {
						beforeClass.add(method);
					}
					if (method.isAnnotationPresent(Before.class)) {
						before.add(method);
					}
					if (method.isAnnotationPresent(AfterClass.class)) {
						afterClass.add(method);
					}
					if (method.isAnnotationPresent(After.class)) {
						after.add(method);
					}
					if (method.isAnnotationPresent(Time.class)) {
						repetitions += method.getAnnotation(Time.class).repeat();
					}
				}
				for(Method method : beforeClass) {
					method.setAccessible(true);
					method.invoke(object);
				}
				long times = 0;
				for(Method method : cls.getDeclaredMethods()) {
					if (method.isAnnotationPresent(Time.class)) {
						Series<Number, Number> data = new Series<>();
						for(int i=1; i<=method.getAnnotation(Time.class).repeat(); i++) {
							for(Method bef : before) {
								bef.setAccessible(true);
								bef.invoke(object);
							}
							runWithTimeout(method, timeMethod, data, i, method.getAnnotation(Time.class).timeout()).await();
							for(Method aft : after) {
								aft.setAccessible(true);
								aft.invoke(object);
							}
							times++;
							Field graph = object.getClass().getDeclaredField("grapher");
							graph.setAccessible(true);
							((Grapher) graph.get(object)).setProgress(((double)times/(double)repetitions));
						}
					}
				}
				for(Method method : afterClass) {
					method.setAccessible(true);
					method.invoke(object);
				}
				graphFinish(object);
			}
		}
	}

	/**
	 * Finishes a graph by executing the {@link Grapher#finish()} method.
	 * @param object The object the grapher is located in
	 * @throws NoSuchFieldException If the field doesn't exist
	 * @throws SecurityException If there is a Security Manager encounters something
	 * @throws NoSuchMethodException If the method doesn't exist
	 * @throws IllegalAccessException If you're not allowed to access something
	 * @throws IllegalArgumentException If the arguments given are not correct
	 * @throws InvocationTargetException If you're invoking the method on an incorrect object
	 */
	private static void graphFinish(Object object) throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Field grapherField = object.getClass().getDeclaredField("grapher");
		grapherField.setAccessible(true);
		Method graphFinish = grapherField.getType().getDeclaredMethod("finish");
		graphFinish.setAccessible(true);
		graphFinish.invoke(grapherField.get(object));
	}

	/**
	 * Internal method used to add data to the graph
	 * @param method The method to run
	 * @param chart The chart to add the data to
	 * @param x The x component
	 * @param y The y component
	 * @throws NoSuchFieldException If the field doesn't exist
	 * @throws SecurityException If the security managers runs into a security violation
	 * @throws IllegalArgumentException When a method doesn't have the given arguments
	 * @throws IllegalAccessException When the reflector cannot access something
	 */
	private static void graphData(Method method, Series<Number, Number> chart, long x, long y) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Platform.runLater(() -> {
			try {
				chart.setName(method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1));
				Field graph = object.getClass().getDeclaredField("grapher");
				graph.setAccessible(true);
				Field counter = object.getClass().getDeclaredField("counter");
				counter.setAccessible(true);
				counter.set(object, x);
				Field graphMax = graph.getType().getDeclaredField("max");
				if (y < ((double) graphMax.get(graph.get(object)))) {
					chart.getData().add(new XYChart.Data<>(x, y));
				}
				if (!((Grapher) graph.get(object)).scatterPlot.getData().contains(chart) && chart.getData().size() != 0) {
					((Grapher) graph.get(object)).scatterPlot.getData().add(chart);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Runs a method with a given timeout
	 * @param method The method to run
	 * @param timeMethod The time method to use to time it
	 * @param data The series to add the data to
	 * @param i The current repetition
	 * @param timeout The timeout, in milliseconds
	 * @return A countdown latch so that the timer can wait
	 */
	private static CountDownLatch runWithTimeout(Method method, TimeMethod timeMethod, Series<Number, Number> data, long i, long timeout) {
		try {
			if (timeout == -1l) {
				timer = Long.MAX_VALUE;
			} else {
				timer = timeout;
			}
			CountDownLatch latch = new CountDownLatch(1);
			CyclicBarrier gate = new CyclicBarrier(3);
			Thread runnable = new Thread(() -> {
				try {
					gate.await();
					method.setAccessible(true);
					long startTime = timeMethod.timeMethod();
					method.invoke(object);
					time.interrupt();
					graphData(method, data, i, timeMethod.timeMethod()-startTime);
					latch.countDown();
				} catch (ReflectiveOperationException | BrokenBarrierException e) {
					// Ignore any exceptions related to reflection or barriers
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			time = new Thread(() -> {
				try {
					gate.await();
					long milliseconds = (long) Math.floor(timer / 1000000);
					Thread.sleep(milliseconds, (int) (timer-milliseconds*1000000));
					runnable.interrupt();
					graphData(method, data, i, timeMethod.convertNano(timer));
					latch.countDown();
				} catch (Exception e) {
					// Do nothing
				}
			});
			runnable.start();
			time.start();
			gate.await();
			return latch;
		} catch (InterruptedException | BrokenBarrierException e) {
			return new CountDownLatch(0);
		}
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new LinkedList<>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		LinkedList<Class<?>> classes = new LinkedList<>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}
	
	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new LinkedList<>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	/**
	 * An interface used to change how
	 * things are timed.
	 * By default, {@link TimeMethod#timeMethod()} uses {@link System#nanoTime()}
	 * and a method to convert nanoseconds into your custom timing method
	 */
	public interface TimeMethod {
		public long timeMethod();
		public long convertNano(long nano);
	}

}
