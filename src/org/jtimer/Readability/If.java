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
package org.jtimer.Readability;

import org.jtimer.Exceptions.NotProperlyInitializedException;

/**
 * This is a fancy ternary operator just used for code-readability.
 * 
 * @author MagneticZero
 *
 * @param <V> The return value type
 */
public class If<V> {

	/**
	 * The conditional that will be checked.
	 */
	private boolean conditional;
	/**
	 * If the conditional is true, this value will be returned.
	 */
	private V thenValue;
	/**
	 * If the {@link org.jtimer.Readability.If#Then(Object) Then} has been executed.
	 */
	private boolean thenExecuted = false;
	/**
	 * If the conditional is false, this value will be returned.
	 */
	private V elseValue;

	/**
	 * Intialize the {@link org.jtimer.Readability.If If} object with the initial
	 * boolean.
	 * 
	 * @param conditional The boolean to test
	 */
	public If(boolean conditional) {
		this.conditional = conditional;
	}

	/**
	 * This is used to set the value that is returned if the boolean is true.
	 * 
	 * @param thenValue The value to return if true
	 * @return Returns the If object for convenience
	 */
	public If<V> Then(V thenValue) {
		this.thenValue = thenValue;
		thenExecuted = true;
		return this;
	}

	/**
	 * This is used to set the value that is returned if the boolean is false. This
	 * will also evaluate the expression and return the correct value.
	 * 
	 * @param elseValue The value to return if false
	 * @return Returns the proper value
	 * @throws NotProperlyInitializedException If
	 *                                         {@link org.jtimer.Readability.If#Then(Object)
	 *                                         Then} was not used before this.
	 */
	public V Else(V elseValue) throws NotProperlyInitializedException {
		this.elseValue = elseValue;
		return exec();
	}

	/**
	 * Executes the If object, this is a private method that gets executed right
	 * after the else method is used.
	 * 
	 * @return Returns the proper value
	 * @throws NotProperlyInitializedException If
	 *                                         {@link org.jtimer.Readability.If#Then(Object)
	 *                                         Then} was not used before this.
	 */
	private V exec() throws NotProperlyInitializedException {
		if (!thenExecuted) {
			throw new NotProperlyInitializedException();
		}
		return conditional ? thenValue : elseValue;
	}

}
