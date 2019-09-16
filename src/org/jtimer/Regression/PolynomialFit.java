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
 * Used to fit a set of data to polynomial.
 * 
 * @author MagneticZero
 */
public class PolynomialFit extends Regression {

	/**
	 * The coefficients of the regression.
	 */
	double[] coefficients;

	/**
	 * Creates a PolynomialFit that will over-fit the data provided.
	 * 
	 * @param xs The x data
	 * @param ys The y data
	 */
	public PolynomialFit(double[] xs, double[] ys) {
		this(xs, ys, xs.length);
	}

	/**
	 * Creates a higher order PolynomialFit that is in the form of f(x) =
	 * C+x+...+x<sup>terms-1</sup>.
	 * 
	 * @param xs    The x data
	 * @param ys    The y data
	 * @param terms The number of terms
	 */
	public PolynomialFit(double[] xs, double[] ys, int terms) {
		coefficients = new double[terms];
		Matrix xsMatrix = new Matrix(xs.length, terms);
		for (int i = 0; i < xs.length; i++) {
			for (int j = 0; j < terms; j++) {
				xsMatrix.toArray()[i][j] = Math.pow(xs[i], j);
			}
		}
		Matrix ysMatrix = new Matrix(ys.length, 1);
		for (int i = 0; i < ys.length; i++) {
			ysMatrix.toArray()[i][0] = ys[i];
		}
		Matrix solutionMatrix = xsMatrix.transpose().multiply(xsMatrix).inverse().multiply(xsMatrix.transpose().multiply(ysMatrix));
		for (int i = 0; i < terms; i++) {
			coefficients[i] = solutionMatrix.toArray()[i][0];
		}
		error(xs, ys);
	}

	/**
	 * Calculates f(x) using the generated regression.
	 * 
	 * @param x The x to use
	 * @return f(x) aka y
	 */
	@Override
	public double calculate(double x) {
		double result = 0;
		for (int i = 0; i < coefficients.length; i++) {
			result += coefficients[i] * Math.pow(x, i);
		}
		return result;
	}
}
