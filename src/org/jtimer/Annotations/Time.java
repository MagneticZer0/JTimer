/*
 * JTimer is a Java library that contains various methods and annotations that allow's one to 
 * time various methods and output it into a graph.
 * Copyright (C) 2019  Harley Merkaj
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://github.com/MagneticZer0/JTimer/blob/master/LICENSE> 
 * or <https://www.gnu.org/licenses/>.
 */
package org.jtimer.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specifiy that this method should be timed.
 * <br>
 * {@link org.jtimer.Annotations.Time#repeat() repeat()} Has a repeat option, by
 * default this is 10. This is the number of times that the test will be
 * repeated.
 * <br>
 * {@link org.jtimer.Annotations.Time#timeout() timeout()} Has a timeout time,
 * by default there is no timeout. The timeout is in nanosecond and a test will
 * be be halted after the timeout has been reached.
 * 
 * @author MagneticZero
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
public @interface Time {
	/**
	 * The amount of times that this method will be executed. By default this is 10.
	 * 
	 * @return The repetitions
	 */
	int repeat() default 10;

	/**
	 * A time in nanoseconds for the test to time-out. By default there is no
	 * timeout.
	 * 
	 * @return The timeout, in nanoseconds
	 */
	long timeout() default -1; // In nanoseconds
}
