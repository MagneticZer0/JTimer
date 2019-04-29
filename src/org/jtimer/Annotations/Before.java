package org.jtimer.Annotations;

import java.lang.annotation.*;

/**
 * Used to specificy that a method should run before each repetition of a
 * time-loop
 * 
 * @author MagneticZero
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Before {

}
