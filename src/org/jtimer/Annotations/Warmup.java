package org.jtimer.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify an amount of non-timed repetitions for the methods so that
 * the CPU can warm up. This does seem to have a tangible effect. The graph of
 * the Runner after a warmup seems to be more consistent.
 * <br>
 * {@link Warmup#iterations() iterations()} Has an iterations count, by default
 * this is 10 although it can be changed if you want.
 * 
 * @author MagneticZero
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Warmup {
	/**
	 * The number of times the warmup will repeat a method.
	 * 
	 * @return The number of iterations
	 */
	int iterations() default 10;
}
