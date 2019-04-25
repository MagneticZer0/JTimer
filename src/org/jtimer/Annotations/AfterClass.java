package org.jtimer.Annotations;

import java.lang.annotation.*;

/**
 * Used to specify that a method should run at the
 * beginning of all time-loops. Most likely use case is
 * to instantiate various variables.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterClass {

}
