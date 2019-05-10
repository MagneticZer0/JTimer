package org.jtimer;

import org.jtimer.Annotations.*;

import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * The brains behind everything I'd say. This is what times methods, adds
 * timeout to methods, adds the data to a graph, recursively finds all methods
 * inside a package, etc. It does a lot and I should probably split things up a
 * bit just to make it look better.
 * 
 * @author MagneticZero
 */
public class Runner {

	/**
	 * The graph to put data in
	 */
	private static Grapher grapher = Grapher.start();
	/**
	 * The test class that is being run
	 */
	private static Object object;
	/**
	 * The thread use to track timeout
	 */
	private static Thread time = new Thread();
	/**
	 * The timer to track timeout
	 */
	private static long timer = -1l;
	/**
	 * Used if someone wants to await the runner
	 */
	private static CountDownLatch latch = new CountDownLatch(2);

	/**
	 * Since everything is static there is no need to be able to instantiate a new
	 * instance of Runner
	 */
	private Runner() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Runs all @{@link org.jtimer.Annotations.Time} methods inside of the package pkg.
	 * 
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
	 * This will execute all methods found in the timing package The order of
	 * operations is anything with a @{@link org.jtimer.Annotations.BeforeClass}
	 * which is only once. Then @{@link org.jtimer.Annotations.Before} is executed
	 * before @{@link org.jtimer.Annotations.Time}, just like
	 * JUnit @{@link org.jtimer.Annotations.Time} will execute followed
	 * by @{@link org.jtimer.Annotations.After}.
	 * Lastly @{@link org.jtimer.Annotations.AfterClass} is executed after
	 * all @{@link org.jtimer.Annotations.Time} has been executed
	 *
	 * @param pkg        The package name that contains the things you want to time
	 * @param timeMethod The functional interface for timing however you want
	 * @throws Throwable Any exceptions not handled will be thrown
	 */
	public static void time(String pkg, TimeMethod timeMethod) throws Throwable {
		for (Class<?> cls : getClasses(pkg)) {
			if (isInstantiable(cls)) {
				Constructor<?> constructor = cls.getDeclaredConstructor(); // This is to access any protected classes
				constructor.setAccessible(true);
				object = constructor.newInstance(); //
				List<Method> beforeClass = new LinkedList<>();
				List<Method> before = new LinkedList<>();
				List<Method> time = new LinkedList<>();
				List<Method> after = new LinkedList<>();
				List<Method> afterClass = new LinkedList<>();
				long repetitions = 0;
				for (Method method : cls.getDeclaredMethods()) {
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
						time.add(method);
						repetitions += method.getAnnotation(Time.class).repeat();
					}
				}
				for (Method method : beforeClass) {
					method.setAccessible(true);
					method.invoke(object);
				}
				long times = 0;
				for (Method method : time) {
					Series<Number, Number> data = new Series<>();
					for (int i = 1; i <= method.getAnnotation(Time.class).repeat(); i++) {
						for (Method bef : before) {
							bef.setAccessible(true);
							bef.invoke(object);
						}
						runWithTimeout(method, timeMethod, data, i, method.getAnnotation(Time.class).timeout()).await();
						for (Method aft : after) {
							aft.setAccessible(true);
							aft.invoke(object);
						}
						times++;
						grapher.setProgress(((double) times / repetitions));
					}
				}
				for (Method method : afterClass) {
					method.setAccessible(true);
					method.invoke(object);
				}
				graphFinish();
			}
		}
		latch.countDown();
	}

	/**
	 * Returns the grapher being used so that the user can set the graph settings.
	 * 
	 * @return The graph being used
	 */
	public static Grapher getGrapher() {
		return grapher;
	}

	/**
	 * Finishes a graph by executing the {@link Grapher#finish()} method.
	 * 
	 * @throws NoSuchFieldException      If the field doesn't exist
	 * @throws SecurityException         If there is a Security Manager encounters
	 *                                   something
	 * @throws NoSuchMethodException     If the method doesn't exist
	 * @throws IllegalAccessException    If you're not allowed to access something
	 * @throws IllegalArgumentException  If the arguments given are not correct
	 * @throws InvocationTargetException If you're invoking the method on an
	 *                                   incorrect object
	 */
	private static void graphFinish() throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method graphFinish = grapher.getClass().getDeclaredMethod("finish");
		graphFinish.setAccessible(true);
		graphFinish.invoke(grapher);
	}

	/**
	 * Internal method used to add data to the graph
	 * 
	 * @param method The method to run
	 * @param chart  The chart to add the data to
	 * @param x      The x component
	 * @param y      The y component
	 * @throws NoSuchFieldException     If the field doesn't exist
	 * @throws SecurityException        If the security managers runs into a
	 *                                  security violation
	 * @throws IllegalArgumentException When a method doesn't have the given
	 *                                  arguments
	 * @throws IllegalAccessException   When the reflector cannot access something
	 */
	private static void graphData(Method method, Series<Number, Number> chart, long x, long y) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Platform.runLater(() -> {
			try {
				if (method.getAnnotation(Time.class).name().equals("")) {
				chart.setName(method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1));
				} else {
					chart.setName(method.getAnnotation(Time.class).name());
				}
				if (Arrays.stream(object.getClass().getDeclaredFields()).anyMatch(field -> field.getName().equals("counter"))) {
					Field counter = object.getClass().getDeclaredField("counter");
					counter.setAccessible(true);
					counter.set(object, x);
				}
				Field graphMax = grapher.getClass().getDeclaredField("max");
				graphMax.setAccessible(true);
				if (y < ((double) graphMax.get(grapher))) {
					chart.getData().add(new XYChart.Data<>(x, y));
				}
				if (!grapher.scatterPlot.getData().contains(chart) && chart.getData().size() != 0) {
					grapher.scatterPlot.getData().add(chart);
				}
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			latch.countDown();
		});
	}

	/**
	 * Runs a method with a given timeout
	 * 
	 * @param method     The method to run
	 * @param timeMethod The time method to use to time it
	 * @param data       The series to add the data to
	 * @param i          The current repetition
	 * @param timeout    The timeout, in milliseconds
	 * @return A countdown latch so that the timer can wait
	 */
	private static CountDownLatch runWithTimeout(Method method, TimeMethod timeMethod, Series<Number, Number> data, long i, long timeout) {
		try {
			if (timeout < 0) {
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
					if (!Thread.interrupted()) {
						graphData(method, data, i, timeMethod.timeMethod() - startTime);
					}
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
					Thread.sleep(milliseconds, (int) (timer - milliseconds * 1000000));
					runnable.interrupt();
					if (!Thread.interrupted()) {
						graphData(method, data, i, timeMethod.convertNano(timer));
					}
					latch.countDown();
				} catch (Exception e) {
					// Do nothing for now
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
	 * Provides a method that allows one to wait for the runner to execute all
	 * things that need timing if they want to execute more things after it.
	 * 
	 * @throws InterruptedException If the latch throws an InterruptedException
	 */
	public static void await() throws InterruptedException {
		latch.await();
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException When the class cannot be found
	 * @throws IOException            Any Input/Output exception
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
		List<Class<?>> classes = new LinkedList<>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base
	 *                    directory
	 * @return The classes
	 * @throws ClassNotFoundException When the class cannot be found
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
	 * Tells if a class is instantiable by checking various thing
	 * 
	 * @param cls The class to see
	 * @return If it is instantiable
	 */
	private static boolean isInstantiable(Class<?> cls) {
		if (cls.getEnclosingClass() != null) {
			return !(cls.isAnnotation() || cls.isArray() || cls.isInterface() || cls.isEnum() || cls.isPrimitive() || Modifier.isAbstract(cls.getModifiers()) || !isInstantiable(cls.getEnclosingClass()));
		} else {
			return !(cls.isAnnotation() || cls.isArray() || cls.isInterface() || cls.isEnum() || cls.isPrimitive() || Modifier.isAbstract(cls.getModifiers()));
		}
	}

	/**
	 * An interface used to change how things are timed. By default,
	 * {@link TimeMethod#timeMethod()} uses {@link System#nanoTime()} and a method
	 * to convert nanoseconds into your custom timing method
	 */
	public interface TimeMethod {
		/**
		 * A way to get System time however you want it, by default this is
		 * {@link System#nanoTime()}
		 * 
		 * @return A time
		 */
		public long timeMethod();

		/**
		 * A way to convert nanoseconds into the method that you're using to time
		 * 
		 * @param nano Nanosecond input
		 * @return Nanoseconds to your time method
		 */
		public long convertNano(long nano);
	}

}
