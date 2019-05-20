package org.jtimer.Annotations;

import java.lang.annotation.*;

/**
 * Used to specify that you want to rename the graph item to something other
 * than the method name. This can include spaces, emojis, whatever else you
 * want. 
 * <br>
 * {@link org.jtimer.Annotations.DisplayName#value() value()} Here is where you
 * can enter the name of the legend item in the graph, if you so please.
 * 
 * @author MagneticZero
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface DisplayName {
	/**
	 * The value that stores the display name.
	 * 
	 * @return The display name
	 */
	String value() default "";
}
