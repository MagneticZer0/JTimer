package org.jtimer;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.jtimer.Annotations.After;
import org.jtimer.Annotations.AfterClass;
import org.jtimer.Annotations.Before;
import org.jtimer.Annotations.BeforeClass;
import org.jtimer.Annotations.DisplayName;
import org.jtimer.Annotations.Settings;
import org.jtimer.Annotations.Time;
import org.jtimer.Annotations.Warmup;
import org.jtimer.Annotations.Handler.AnnotationHandler;
import org.jtimer.Collections.AnnotationMap;
import org.jtimer.Exceptions.Handler.PopupDialogue;
import org.jtimer.Misc.Setting;

import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

/**
 * The brains behind everything I'd say. This is what times methods, adds
 * timeout to methods, adds the data to a graph, recursively finds all methods
 * inside a package, etc. It does a lot and I should probably split things up a
 * bit just to make it look better. To get a more detailed look at what the
 * runner does make sure to look at the time method.
 * 
 * @see org.jtimer.Runner#time(String, TimeMethod)
 * 
 * @author MagneticZero
 */
public class Runner {

	/**
	 * The {@link org.jtimer.Grapher graph} to put the data collected in.
	 */
	private static Grapher grapher = Grapher.start();
	/**
	 * The test class that is being run.
	 */
	private static Object object;
	/**
	 * The {@link java.lang.Thread thread} use to track timeout.
	 */
	private static Thread time = new Thread();
	/**
	 * The timer to track timeout.
	 */
	private static long timer = -1;
	/**
	 * Used if someone wants to {@link java.util.concurrent.CountDownLatch#await()
	 * await} the {@link org.jtimer.Runner runner}.
	 */
	private static CountDownLatch latch = new CountDownLatch(2);
	/**
	 * A {@link org.jtimer.Collections.AnnotationMap annotation map} of
	 * {@link java.lang.reflect.Method methods} that the {@link org.jtimer.Runner
	 * runner} will {@link java.lang.reflect.Method#invoke(Object, Object...)
	 * execute}.
	 */
	private static AnnotationMap<Method> methods;
	/**
	 * Since creating a {@link org.jtimer.Annotations.Handler.AnnotationHandler
	 * AnnotationHandler} could be quite taxing with meta annotations, a way of
	 * mapping methods to their
	 * {@link org.jtimer.Annotations.Handler.AnnotationHandler annotation handlers}
	 * would be efficient.
	 */
	private static HashMap<Method, AnnotationHandler> methodHandlers;
	/**
	 * This {@link org.jtimer.Exceptions.Handler.PopupDialogue PopupDialogue} is
	 * responsible for catching and logging exceptions caused by running JTimer.
	 */
	private static PopupDialogue exceptionCatcher = new PopupDialogue("Exception Catcher", true, "Ignore and Continue", true, "Exit JTimer");

	/**
	 * Since everything is static there is no need to be able to instantiate a new
	 * instance of {@link org.jtimer.Runner Runner}.
	 */
	private Runner() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Runs all {@link org.jtimer.Annotations.Time @Time} methods inside of the
	 * {@link java.lang.Class Class} cls. See
	 * {@link org.jtimer.Runner#time(String, TimeMethod) Runner.time(String,
	 * TimeMethod)} for a more detailed method that does the same thing.
	 * 
	 * @see Runner#time(String, TimeMethod)
	 * 
	 * @param cls The class that contains the things you want to time
	 * @throws Throwable This could be a number of things, although most things
	 *                   should be already handeled by the {@link org.jtimer.Runner
	 *                   Runner}, there's a special case though if the
	 *                   {@link java.lang.Exception Exception} is a
	 *                   {@link java.lang.reflect.InvocationTargetException
	 *                   InvocationTargetException} then the Runner will unwrap the
	 *                   exception beforehand and then throw that.
	 */
	public static void time(Class<?> cls) throws Throwable {
		try {
			time(cls.getCanonicalName() + ".class");
		} catch (Throwable e) {
			exceptionCatcher.writeError((Exception) e);
			throw e;
		}
	}

	/**
	 * Runs all {@link org.jtimer.Annotations.Time @Time} methods inside of the
	 * {@link java.lang.Class Class} cls using a specified
	 * {@link org.jtimer.Runner.TimeMethod TimeMethod}. See
	 * {@link org.jtimer.Runner#time(String, TimeMethod) Runner.time(String,
	 * TimeMethod)} for a more detailed method that does the same thing.
	 * 
	 * @see Runner#time(String, TimeMethod)
	 * 
	 * @param cls        The class that contains the things you want to time
	 * @param timeMethod The functional interface for timing however you want
	 * @throws Throwable This could be a number of things, although most things
	 *                   should be already handeled by the {@link org.jtimer.Runner
	 *                   Runner}, there's a special case though if the
	 *                   {@link java.lang.Exception Exception} is a
	 *                   {@link java.lang.reflect.InvocationTargetException
	 *                   InvocationTargetException} then the Runner will unwrap the
	 *                   exception beforehand and then throw that.
	 */
	public static void time(Class<?> cls, TimeMethod timeMethod) throws Throwable {
		try {
			time(cls.getCanonicalName() + ".class", timeMethod);
		} catch (Throwable e) {
			exceptionCatcher.writeError((Exception) e);
			throw e;
		}
	}

	/**
	 * Runs all {@link org.jtimer.Annotations.Time @Time} methods inside of the
	 * package pkg. See {@link org.jtimer.Runner#time(String, TimeMethod)
	 * Runner.time(String, TimeMethod)} for a more detailed method that does the same
	 * thing.
	 * 
	 * @see Runner#time(String, TimeMethod)
	 * 
	 * @param pkg The package name that contains the things you want to time
	 * @throws Throwable This could be a number of things, although most things
	 *                   should be already handeled by the {@link org.jtimer.Runner
	 *                   Runner}, there's a special case though if the
	 *                   {@link java.lang.Exception Exception} is a
	 *                   {@link java.lang.reflect.InvocationTargetException
	 *                   InvocationTargetException} then the Runner will unwrap the
	 *                   exception beforehand and then throw that.
	 */
	public static void time(String pkg) throws Throwable {
		try {
			time(pkg, nano -> nano);
		} catch (Throwable e) {
			exceptionCatcher.writeError((Exception) e);
			throw e;
		}
	}

	/**
	 * This will {@link java.lang.reflect.Method#invoke(Object, Object...) execute}
	 * all {@link java.lang.reflect.Method methods} found in the timing package,
	 * although if the String pkg ends with .class, it will only do the following on
	 * that class. If the class has a {@link org.jtimer.Annotations.Warmup @Warmup}
	 * then all methods with the {@link org.jtimer.Annotations.Time @Time} will be
	 * executed by the {@link org.jtimer.Runner runner} a predefined amount of
	 * times. The order of operations is anything with a
	 * {@link org.jtimer.Annotations.BeforeClass @BeforeClass} which is only once.
	 * Then {@link org.jtimer.Annotations.Before @Before} is executed before each
	 * {@link org.jtimer.Annotations.Time @Time}, just like JUnit
	 * {@link org.jtimer.Annotations.Time @Time} will execute followed by
	 * {@link org.jtimer.Annotations.After @After}. Lastly
	 * {@link org.jtimer.Annotations.AfterClass @AfterClass} is executed after all
	 * {@link org.jtimer.Annotations.Time @Time} have been executed.
	 *
	 * @param pkg        The package name that contains the things you want to time
	 * @param timeMethod The functional interface for timing however you want
	 * @throws Throwable This could be a number of things, although most things
	 *                   should be already handeled by the {@link org.jtimer.Runner
	 *                   Runner}, there's a special case though if the
	 *                   {@link java.lang.Exception Exception} is a
	 *                   {@link java.lang.reflect.InvocationTargetException
	 *                   InvocationTargetException} then the Runner will unwrap the
	 *                   exception beforehand and then throw that. And now with the
	 *                   creation of
	 *                   {@link org.jtimer.Exceptions.Catcher.PopupDialogue
	 *                   PopupDialogue} exceptions will be logged and saved so that
	 *                   they can be uploaded and further analyzed. The exceptions
	 *                   will still be thrown, so catching and handling will be
	 *                   still be up to the user.
	 */
	public static void time(String pkg, TimeMethod timeMethod) throws Throwable {
		try {
			methods = new AnnotationMap<>();
			methodHandlers = new HashMap<>();
			Class<?>[] classes;
			if (pkg.contains(".class")) {
				classes = new Class[] { Class.forName(pkg.replace(".class", "")) };
			} else {
				classes = getClasses(pkg);
			}
			for (Class<?> cls : classes) {
				if (isInstantiable(cls)) {
					Constructor<?> constructor = cls.getDeclaredConstructor(); // This is to access any protected classes
					constructor.setAccessible(true);                           //
					object = constructor.newInstance();                        //
					long repetitions = 0;
					for (Method method : cls.getDeclaredMethods()) {
						methodHandlers.put(method, new AnnotationHandler(method));
						methods.put(methodHandlers.get(method).getAnnotations(), method);
						if (methodHandlers.get(method).isAnnotationPresent(Time.class)) {
							repetitions += methodHandlers.get(method).getAnnotation(Time.class).repeat();
						}
					}
					warmup(constructor.newInstance(), timeMethod); // So instance variables are left default
					for (Method method : methods.get(BeforeClass.class)) {
						method.setAccessible(true);
						method.invoke(object);
					}
					long times = 0;
					for (Method method : methods.get(Time.class)) {
						Series<Number, Number> data = new Series<>();
						for (int i = 1; i <= methodHandlers.get(method).getAnnotation(Time.class).repeat(); i++) {
							for (Method bef : methods.get(Before.class)) {
								bef.setAccessible(true);
								bef.invoke(object);
							}
							runWithTimeout(method, timeMethod, data, i, methodHandlers.get(method).getAnnotation(Time.class).timeout(), object, false).await();
							for (Method aft : methods.get(After.class)) {
								aft.setAccessible(true);
								aft.invoke(object);
							}
							times++;
							grapher.setProgress((double) times / repetitions, false);
						}
					}
					for (Method method : methods.get(AfterClass.class)) {
						method.setAccessible(true);
						method.invoke(object);
					}
					AnnotationHandler clsHandler = new AnnotationHandler(cls);
					graphFinish(clsHandler.isAnnotationPresent(Settings.class) && Arrays.stream(clsHandler.getAnnotation(Settings.class).value()).anyMatch(x -> x.equals(Setting.BEST_FIT)));
				}
			}
			latch.countDown();
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} catch (Throwable e) {
			exceptionCatcher.writeError((Exception) e);
			throw e;
		}
	}

	/**
	 * Returns the {@link org.jtimer.Grapher grapher} being used so that the user
	 * can set the graph settings.
	 * 
	 * @return The {@link org.jtimer.Grapher graph} being used
	 */
	public static Grapher getGrapher() {
		return grapher;
	}

	/**
	 * Provides a method that allows one to wait for the {@link org.jtimer.Runner
	 * runner} to execute all things that need timing if they want to execute more
	 * things after it.
	 * 
	 * @throws InterruptedException If the {@link org.jtimer.Runner#latch latch}
	 *                              throws an {@link java.lang.InterruptedException
	 *                              InterruptedException}
	 */
	public static void await() throws InterruptedException {
		latch.await();
		grapher.await();
		Thread.sleep(1000); // Wait until everything is properly graphed
	}

	/**
	 * Executed to warmup all the {@link java.lang.reflect.Method methods} and your
	 * CPU, if you want. The class needs the
	 * {@link org.jtimer.Annotations.Warmup @Warmup} annotation present in order for
	 * this to be run. If present, it will run all
	 * {@link org.jtimer.Annotations.Time @Time} annotated methods the defined
	 * amount of times. This also handles all the class static variables by keeping
	 * track of them and resetting them after the warmup has been executed back to
	 * the initial values. It basically does all the same things
	 * {@link org.jtimer.Runner#time(String) Runner.time(String)}.
	 * 
	 * @param obj        The object to use
	 * @param timeMethod The functional interface for timing however you want
	 * @throws IllegalArgumentException  If the proper arguments are not given
	 * @throws IllegalAccessException    If a {@link java.lang.reflect.Method
	 *                                   method}/{@link java.lang.reflect.Field
	 *                                   field} is not accessible
	 * @throws InvocationTargetException If the {@link java.lang.reflect.Method
	 *                                   method} called throws an exception
	 * @throws NoSuchFieldException      If a {@link java.lang.reflect.Field field}
	 *                                   doesn't exist
	 * @throws SecurityException         If the security manager throws an exception
	 * @throws InterruptedException      If the latches/barriers throw an exception
	 */
	private static void warmup(Object obj, TimeMethod timeMethod) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, SecurityException, InterruptedException {
		AnnotationHandler objHandler = new AnnotationHandler(obj.getClass());
		if (objHandler.isAnnotationPresent(Warmup.class)) {
			HashMap<Field, Object> staticFieldValues = new HashMap<>(); // Since static fields are shared, we need to keep track of the values
			for (Field field : obj.getClass().getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
					field.setAccessible(true);
					staticFieldValues.put(field, field.get(null));
				}
			}
			for (Method method : methods.get(BeforeClass.class)) {
				method.setAccessible(true);
				method.invoke(obj);
			}
			long warmup = 0;
			long total = objHandler.getAnnotation(Warmup.class).iterations() * methods.get(Time.class).size();
			for (Method method : methods.get(Time.class)) {
				for (int i = 0; i < objHandler.getAnnotation(Warmup.class).iterations(); i++) {
					for (Method bef : methods.get(Before.class)) {
						bef.setAccessible(true);
						bef.invoke(obj);
					}
					if (Arrays.stream(obj.getClass().getDeclaredFields()).anyMatch(field -> field.getName().equals("counter"))) {
						Field counter = obj.getClass().getDeclaredField("counter");
						counter.setAccessible(true);
						counter.set(obj, i);
					}
					runWithTimeout(method, timeMethod, null, i, methodHandlers.get(method).getAnnotation(Time.class).timeout(), obj, true).await();
					for (Method aft : methods.get(After.class)) {
						aft.setAccessible(true);
						aft.invoke(obj);
					}
					warmup++;
					grapher.setProgress((double) warmup / total, true);
				}
			}
			for (Method method : methods.get(AfterClass.class)) {
				method.setAccessible(true);
				method.invoke(obj);
			}
			for (Field field : staticFieldValues.keySet()) {
				field.set(null, staticFieldValues.get(field)); // Reset all static values
			}
		}
	}

	/**
	 * Runs a {@link java.lang.reflect.Method method} with a given timeout.
	 * 
	 * @param method     The {@link java.lang.reflect.Method method} to run
	 * @param timeMethod The {@link org.jtimer.Runner.TimeMethod time method} to use
	 *                   to time it
	 * @param data       The series to add the data to
	 * @param i          The current repetition
	 * @param timeout    The timeout, in milliseconds
	 * @param obj        The object to execute to use
	 * @param warmup     If this is a warmup
	 * @return A countdown latch so that the timer can wait
	 */
	private static CountDownLatch runWithTimeout(Method method, TimeMethod timeMethod, Series<Number, Number> data, long i, long timeout, Object obj, boolean warmup) {
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
					long startTime = System.nanoTime();
					method.invoke(obj);
					time.interrupt();
					if (!Thread.interrupted() && !warmup) {
						graphData(method, data, i, timeMethod.convertNano(System.nanoTime() - startTime));
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
					if (!Thread.interrupted() && !warmup) {
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
	 * Internal method used to add data to the graph.
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
				if (!methodHandlers.get(method).isAnnotationPresent(DisplayName.class)) {
					chart.setName(method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1));
				} else {
					chart.setName(methodHandlers.get(method).getAnnotation(DisplayName.class).value());
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
				if (!grapher.plot.getData().contains(chart) && chart.getData().size() != 0) {
					grapher.plot.getData().add(chart);
				}
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			latch.countDown();
		});
	}

	/**
	 * Finishes a graph by executing the {@link org.jtimer.Grapher#finish(boolean)
	 * Grapher.finish(boolean)} method.
	 * 
	 * @param bestFit Whether or not the best fit function should be calculated.
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
	private static void graphFinish(boolean bestFit) throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method graphFinish = grapher.getClass().getDeclaredMethod("finish", boolean.class);
		graphFinish.setAccessible(true);
		graphFinish.invoke(grapher, bestFit);
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
	 * Tells if a class is instantiable by checking various thing.
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
	 * {@link TimeMethod#convertNano(long) convertNano()} just returns
	 * the time. Internally, everything is timed in nanoseconds so there is no need
	 * to do a conversion. although if you want you data displayed in second, they
	 * you may want to return something else.
	 */
	public interface TimeMethod {
		/**
		 * A way to convert nanoseconds into the time unit that you're using to time.
		 * <br>
		 * I.E. if my time is in seconds, then I would
		 * <code>return nano*10<sup>-9</sup></code>
		 * 
		 * @param nano Nanosecond input
		 * @return Nanoseconds convert to your time unit
		 */
		public long convertNano(long nano);
	}

}
