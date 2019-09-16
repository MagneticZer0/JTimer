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