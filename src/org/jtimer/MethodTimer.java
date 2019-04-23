package org.jtimer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodTimer {
	
	/**
	 * Times a given method, public or private, it can access them!
	 * @param object, if a method is not static, the object that will execute this
	 * @param cls The class the method is located in
	 * @param mth The method to test
	 * @param args Input the arguments that the method needs followed by the Classes of the arguments
	 * @throws SecurityException Exception to do with Java Reflection
	 * @throws NoSuchMethodException Exception to do with Java Reflection
	 * @throws InvocationTargetException Exception to do with Java Reflection
	 * @throws IllegalArgumentException Exception to do with Java Reflection
	 * @throws IllegalAccessException Exception to do with Java Reflection
	 * @throws InterruptedException Exception to do with Java Reflection
	 * @return The time in nanoseconds (Only as accurate as your CPU)
	 */
	public static long timeStatic(Class<?> cls, String mth, Object...args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException {
		return time((Object) null, cls, mth, args);
	}
	
	/**
	 * Times a given method, public or private, it can access them!
	 * @param object, if a method is not static, the object that will execute this
	 * @param cls The class the method is located in
	 * @param mth The method to test
	 * @param args Input the arguments that the method needs followed by the Classes of the arguments
	 * @throws SecurityException Exception to do with Java Reflection
	 * @throws NoSuchMethodException Exception to do with Java Reflection
	 * @throws InvocationTargetException Exception to do with Java Reflection
	 * @throws IllegalArgumentException Exception to do with Java Reflection
	 * @throws IllegalAccessException Exception to do with Java Reflection
	 * @throws InterruptedException Exception to do with Java Reflection
	 * @return The time in nanoseconds (Only as accurate as your CPU)
	 */
	public static long time(Object object, String mth, Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException {
		return time(object, object.getClass(), mth, args);
	}
	
	/**
	 * Times a given method, public or private, it can access them!
	 * @param timeMethod The method of timing, if none is provided default is nanoseconds
	 * @param object, if a method is not static, the object that will execute this
	 * @param cls The class the method is located in
	 * @param mth The method to test
	 * @param args Input the arguments that the method needs followed by the Classes of the arguments
	 * @throws SecurityException Exception to do with Java Reflection
	 * @throws NoSuchMethodException Exception to do with Java Reflection
	 * @throws InvocationTargetException Exception to do with Java Reflection
	 * @throws IllegalArgumentException Exception to do with Java Reflection
	 * @throws IllegalAccessException Exception to do with Java Reflection
	 * @throws InterruptedException Exception to do with Java Reflection
	 * @return The time in nanoseconds (Only as accurate as your CPU)
	 */
	public static long timeStatic(TimeMethod timeMethod, Class<?> cls, String mth, Object...args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException {
		return time(timeMethod, null, cls, mth, args);
	}
	
	/**
	 * Times a given method, public or private, it can access them!
	 * @param timeMethod The method of timing, if none is provided default is nanoseconds
	 * @param object, if a method is not static, the object that will execute this
	 * @param cls The class the method is located in
	 * @param mth The method to test
	 * @param args Input the arguments that the method needs followed by the Classes of the arguments
	 * @throws SecurityException Exception to do with Java Reflection
	 * @throws NoSuchMethodException Exception to do with Java Reflection
	 * @throws InvocationTargetException Exception to do with Java Reflection
	 * @throws IllegalArgumentException Exception to do with Java Reflection
	 * @throws IllegalAccessException Exception to do with Java Reflection
	 * @throws InterruptedException Exception to do with Java Reflection
	 * @return The time in nanoseconds (Only as accurate as your CPU)
	 */
	public static long time(TimeMethod timeMethod, Object object, String mth, Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException {
		return time(timeMethod, object, object.getClass(), mth, args);
	}

	/**
	 * Times a given method, public or private, it can access them!
	 * @param object, if a method is not static, the object that will execute this
	 * @param cls The class the method is located in
	 * @param mth The method to test
	 * @param args Input the arguments that the method needs followed by the Classes of the arguments
	 * @throws SecurityException Exception to do with Java Reflection
	 * @throws NoSuchMethodException Exception to do with Java Reflection
	 * @throws InvocationTargetException Exception to do with Java Reflection
	 * @throws IllegalArgumentException Exception to do with Java Reflection
	 * @throws IllegalAccessException Exception to do with Java Reflection
	 * @throws InterruptedException Exception to do with Java Reflection
	 * @return The time in nanoseconds (Only as accurate as your CPU)
	 */
	private static long time(Object object, Class<?> cls, String mth, Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException {
		return time(() -> System.nanoTime(), object, cls, mth, args);
	}

	/**
	 * Times a given method, public or private, it can access them!
	 * @param timeMethod The method of timing, if none is provided default is nanoseconds
	 * @param object, if a method is not static, the object that will execute this
	 * @param cls The class the method is located in
	 * @param mth The method to test
	 * @param args Input the arguments that the method needs followed by the Classes of the arguments
	 * @throws SecurityException Exception to do with Java Reflection
	 * @throws NoSuchMethodException Exception to do with Java Reflection
	 * @throws InvocationTargetException Exception to do with Java Reflection
	 * @throws IllegalArgumentException Exception to do with Java Reflection
	 * @throws IllegalAccessException Exception to do with Java Reflection
	 * @throws InterruptedException Exception to do with Java Reflection
	 * @return The time in nanoseconds (Only as accurate as your CPU)
	 */
	public static long time(TimeMethod timeMethod, Object object, Class<?> cls, String mth, Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException {
		Class<?>[] parameterClasses = new Class[args.length/2];
		for(int i=0; i<args.length/2; i++) {
			parameterClasses[i] = (Class<?>) args[args.length/2+i];
		}
		Object[] parameters = new Object[args.length/2];
		for(int i=0; i<args.length/2; i++) {
			parameters[i] = args[i];
		}
		Method method = cls.getDeclaredMethod(mth, parameterClasses);
		method.setAccessible(true);
		long startTime;
		startTime = timeMethod.timeMethod();
		method.invoke(object, parameters);
		return timeMethod.timeMethod()-startTime;
	}
	
	public interface TimeMethod {
		public long timeMethod();
	}
}
