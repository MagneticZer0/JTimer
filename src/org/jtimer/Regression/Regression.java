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
package org.jtimer.Regression;

/**
 * An abstract class that acts as a base-line for the other forms of regression.
 * Has methods to compare regressions, calculate the error, and setting the
 * names and an abstract method for calculating f(x).
 * 
 * @author MagneticZero
 */
public abstract class Regression implements Comparable<Regression> {

	/**
	 * The error for the regression.
	 */
	private Double error = Double.NaN;
	/**
	 * The name for the regression.
	 */
	private String name;

	/**
	 * Calculates the y for the given x using the specified method.
	 * 
	 * @param x The x to input
	 * @return f(x) aka y
	 */
	public abstract double calculate(double x);

	/**
	 * Calculates the error through a pair of known xs and ys.
	 * 
	 * @param xs The x array
	 * @param ys The y array
	 * @return The error generated
	 */
	public double error(double[] xs, double[] ys) {
		if (error.isNaN()) {
			double dist = 0;
			for (int i = 0; i < xs.length; i++) {
				dist += Math.pow(ys[i] - calculate(xs[i]), 2);
			}
			error = Math.sqrt(dist);
		}
		return error;
	}

	/**
	 * Comapres one regression to another by using the error.
	 */
	@Override
	public int compareTo(Regression reg) {
		return (int) Math.round((error - reg.error) * 100);
	}

	/**
	 * A way to get the name.
	 */
	public String toString() {
		return name;
	}

	/**
	 * Sets the name of the Regression, this value is returned by
	 * {@link org.jtimer.Regression.Regression#toString() toString()}.
	 * 
	 * @param name The name to set to
	 * @return Just a convenience
	 */
	public Regression name(String name) {
		this.name = name;
		return this;
	}
}
