package org.jtimer.Annotations;

import java.lang.annotation.*;

/**
 * Used to specify that a method should run at the beginning of all time-loops.
 * Most likely use case is to instantiate various variables.
 * 
 * @author MagneticZero 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface AfterClass {

}
