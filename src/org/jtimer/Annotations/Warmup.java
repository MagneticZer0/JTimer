package org.jtimer.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify an amount of non-timed repetitions for the methods so that
 * the CPU can warm up. I'm not really sure if this has any tangible benefit,
 * although I will test it out.
 * 
 * {@link Warmup#iterations()} Has an iterations count, by default this is 10
 * although it can be changed if you want.
 * 
 * @author MagneticZero
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Warmup {
	int iterations() default 10;
}
