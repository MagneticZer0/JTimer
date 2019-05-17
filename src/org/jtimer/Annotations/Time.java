package org.jtimer.Annotations;

import java.lang.annotation.*;

/**
 * Used to specifiy that this method should be timed.
 * <br></br>
 * {@link Time#repeat()} Has a repeat option, by default this is 10. This is the
 * number of times that the test will be repeated.
 * <br></br>
 * {@link Time#timeout()} Has a timeout time, by default there is no timeout.
 * The timeout is in nanosecond and a test will be be halted after the timeout
 * has been reached.
 * 
 * @author MagneticZero
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface Time {
	int repeat() default 10;

	long timeout() default -1; // In nanoseconds
}
