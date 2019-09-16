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
 * Used to fit a set of data to a certain function.
 * 
 * @author MagneticZero
 */
public class FunctionalFit extends Regression {

	/**
	 * The coefficients of the regression, &beta;<sub>n</sub>.
	 */
	double[] coefficients;
	/**
	 * The function used.
	 */
	Function function;

	/**
	 * Creates a FunctionalFit that is in the form f(x) =
	 * &beta;<sub>1</sub>+&beta;<sub>2</sub>*{@link org.jtimer.Regression.Function
	 * f}(x).
	 * <br><br>
	 * where &beta;<sub>n</sub> is a coefficient determined through regression.
	 * 
	 * @param xs       The x data
	 * @param ys       The y data
	 * @param function The {@link org.jtimer.Regression.Function function} to use
	 */
	public FunctionalFit(double[] xs, double[] ys, Function function) {
		this(xs, ys, function, 2);
	}

	/**
	 * Creates a higher order FunctionalFit that is in the form of f(x) =
	 * &beta;<sub>1</sub>+&beta;<sub>2</sub>*{@link org.jtimer.Regression.Function
	 * f}(x)+...+&beta;<sub>term-1</sub>*{@link org.jtimer.Regression.Function
	 * f}(x)<sup>term-1</sup>.
	 * <br><br>
	 * where &beta;<sub>n</sub> is a coefficient determined through regression.
	 * 
	 * @param xs       The x data
	 * @param ys       The y data
	 * @param function The {@link org.jtimer.Regression.Function function} to use
	 * @param terms    The number of terms
	 */
	public FunctionalFit(double[] xs, double[] ys, Function function, int terms) {
		this.function = function;
		coefficients = new double[terms];
		Matrix xsMatrix = new Matrix(xs.length, terms);
		for (int i = 0; i < xs.length; i++) {
			for (int j = 0; j < terms; j++) {
				xsMatrix.toArray()[i][j] = Math.pow(function.calc(xs[i]), j);
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
			result += coefficients[i] * Math.pow(function.calc(x), i);
		}
		return result;
	}
}
