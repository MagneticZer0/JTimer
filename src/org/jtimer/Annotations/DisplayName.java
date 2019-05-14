package org.jtimer.Annotations;

import java.lang.annotation.*;

/**
 * Used to specify that you want to rename the graph item to something other
 * than the method name. This can include spaces, emojis, whatever else you
 * want.
 * 
 * {@link DisplayName#value()} Here is where you can enter the name of the
 * legend item in the graph, if you so please.
 * 
 * @author MagneticZero
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DisplayName {
	String value();
}
