package org.jtimer.Annotations;

import java.lang.annotation.*;

import org.jtimer.Misc.Setting;

/**
 * A class level annotation that can be used to declare various settings that
 * JTimer can read and do things depending on these settings. 
 * <br>
 * {@link org.jtimer.Annotations.Settings#value() value()} This is a
 * comma-separated array of {@link org.jtimer.Misc.Setting} values.
 * 
 * @author MagneticZero
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Settings {
	/**
	 * Used to store an array of {@link org.jtimer.Misc.Setting Setting} values.
	 * 
	 * @return The {@link org.jtimer.Misc.Setting Setting} value array
	 */
	Setting[] value() default {};
}