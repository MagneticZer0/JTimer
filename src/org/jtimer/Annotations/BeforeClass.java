package org.jtimer.Annotations;

import java.lang.annotation.*;

/**
 * Used to specify that a method should run at the
 * end of all time-loops.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeClass {

}
