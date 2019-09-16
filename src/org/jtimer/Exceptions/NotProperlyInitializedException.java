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
package org.jtimer.Exceptions;

/**
 * Used if an {@link org.jtimer.Readability.If If} object's
 * {@link org.jtimer.Readability.If#Else(Object) Else} method is executed before
 * the {@link org.jtimer.Readability.If#Then(Object) Then} method.
 * 
 * @author MagneticZero
 *
 */
public class NotProperlyInitializedException extends RuntimeException {

	private static final long serialVersionUID = -1923285635403639187L;

}
