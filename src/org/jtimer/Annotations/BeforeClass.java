package org.jtimer.Annotations;

import java.lang.annotation.*;

/**
 * Used to specify that a method should run at the end of all time-loops.
 * 
 * @author MagneticZero
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface BeforeClass {

}
