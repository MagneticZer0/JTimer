package org.jtimer;

import org.jtimer.Annotations.*;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Runner {

	private static String packageName;
	private static Object object;
	private static Thread time = new Thread();
	private static long timer = -1l;

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
		packageName = pkg;
		for(Class<?> cls : getClasses(packageName)) {
			object = cls.newInstance();
			if (!cls.isAnnotation()) {
				List<Method> beforeClass = new LinkedList<>();
				List<Method> before = new LinkedList<>();
				List<Method> afterClass = new LinkedList<>();
				List<Method> after = new LinkedList<>();
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
				}
				for(Method method : beforeClass) {
					method.setAccessible(true);
					method.invoke(object);
				}
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
						}
					}
				}
				for(Method method : afterClass) {
					method.setAccessible(true);
					method.invoke(object);
				}
			}
		}
	}

	private static void graphData(Method method, Series<Number, Number> chart, long x, long y) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
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
		if (!((Grapher) graph.get(object)).data.contains(chart) && chart.getData().size() != 0) {
			((Grapher) graph.get(object)).data.add(chart);
		}
	}
	
	private static CountDownLatch runWithTimeout(Method method, TimeMethod timeMethod, Series<Number, Number> data, long i, long timeout) throws BrokenBarrierException {
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
		} catch (InterruptedException e) {
			return new CountDownLatch(0);
		}
	}

	public void setPkgName(String pkg) {
		packageName = pkg;
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

	public interface TimeMethod {
		public long timeMethod();
		public long convertNano(long nano);
	}

}
