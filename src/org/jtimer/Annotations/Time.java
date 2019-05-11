package org.jtimer.Annotations;

import java.lang.annotation.*;

/**
 * Used to specifiy that this method should be timed.
 * 
 * {@link Time#repeat()} Has a repeat option, by default this is 10. This is the
 * number of times that the test will be repeated.
 * 
 * {@link Time#timeout()} Has a timeout time, by default there is no timeout.
 * The timeout is in nanosecond and a test will be be halted after the timeout
 * has been reached.
 * 
 * {@link Time#name()} Has a name, by default the name will be the method name,
 * although this can be overwritten by using this
 * 
 * @author MagneticZero
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Time {
	int repeat() default 10;

	long timeout() default -1; // In nanoseconds
	
	String name() default "";
}
