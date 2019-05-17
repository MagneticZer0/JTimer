package org.jtimer.Annotations;

import java.lang.annotation.*;

import org.jtimer.Misc.Setting;

/**
 * A class level annotation that can be used to declare various settings that
 * JTimer can read and do things depending on these settings.
 * <br></br>
 * {@link org.jtimer.Annotations.Settings#value()} This is a comma-separated
 * array of Setting values.
 * 
 * @author MagneticZero
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Settings {
	Setting[] value() default {};
}