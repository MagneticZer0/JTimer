package org.jtimer.Annotations;

import java.lang.annotation.*;

/**
 * Used to specifiy that this method should be timed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Time {
	int repeat() default 10;
	long timeout() default -1; // In nanoseconds
}
