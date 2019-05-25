package org.jtimer.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specifiy that this method should be timed.
 * <br>
 * {@link org.jtimer.Annotations.Time#repeat() repeat()} Has a repeat option, by
 * default this is 10. This is the number of times that the test will be
 * repeated.
 * <br>
 * {@link org.jtimer.Annotations.Time#timeout() timeout()} Has a timeout time,
 * by default there is no timeout. The timeout is in nanosecond and a test will
 * be be halted after the timeout has been reached.
 * 
 * @author MagneticZero
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
public @interface Time {
	/**
	 * The amount of times that this method will be executed. By default this is 10.
	 * 
	 * @return The repetitions
	 */
	int repeat() default 10;

	/**
	 * A time in nanoseconds for the test to time-out. By default there is no
	 * timeout.
	 * 
	 * @return The timeout, in nanoseconds
	 */
	long timeout() default -1; // In nanoseconds
}
