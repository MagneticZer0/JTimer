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

import java.util.Collection;

/**
 * Used to fit a set of data to a certain set function of functions.
 * 
 * @author MagneticZero
 */
public class FlexibleFunctionalFit extends Regression {

	/**
	 * The coefficients of the regression, &beta;<sub>n</sub>.
	 */
	double[] coefficients;
	/**
	 * The functions used.
	 */
	Function[] functions;

	/**
	 * Creates a {@link org.jtimer.Regression.FlexibleFunctionalFit
	 * FlexibleFunctionalFit} that is in the form f(x) =
	 * &beta;<sub>1</sub>+&beta;<sub>2</sub>*{@link org.jtimer.Regression.Function
	 * f}<sub>1</sub>(x)+&beta;<sub>3</sub>*{@link org.jtimer.Regression.Function
	 * f}<sub>2</sub>(x)+... using an array of functions.
	 * <br><br>
	 * where &beta;<sub>n</sub> is a coefficient determined through regression.
	 * 
	 * @param xs        The x data
	 * @param ys        The y data
	 * @param functions The {@link org.jtimer.Regression.Function functions} to use
	 */
	public FlexibleFunctionalFit(double[] xs, double[] ys, Function... functions) {
		this.functions = functions;
		coefficients = new double[functions.length + 1];
		Matrix xsMatrix = new Matrix(xs.length, functions.length + 1);
		for (int i = 0; i < xs.length; i++) {
			for (int j = 0; j <= functions.length; j++) {
				xsMatrix.toArray()[i][j] = j == 0 ? 1 : functions[j - 1].calc(xs[i]);
			}
		}
		Matrix ysMatrix = new Matrix(ys.length, 1);
		for (int i = 0; i < ys.length; i++) {
			ysMatrix.toArray()[i][0] = ys[i];
		}
		Matrix solutionMatrix = xsMatrix.transpose().multiply(xsMatrix).inverse().multiply(xsMatrix.transpose().multiply(ysMatrix));
		for (int i = 0; i <= functions.length; i++) {
			coefficients[i] = solutionMatrix.toArray()[i][0];
		}
		error(xs, ys);
	}

	/**
	 * Creates a {@link org.jtimer.Regression.FlexibleFunctionalFit
	 * FlexibleFunctionalFit} that is in the form f(x) =
	 * &beta;<sub>1</sub>+&beta;<sub>2</sub>*{@link org.jtimer.Regression.Function
	 * f}<sub>1</sub>(x)+&beta;<sub>3</sub>*{@link org.jtimer.Regression.Function
	 * f}<sub>2</sub>(x)+... using a collection of functions.
	 * <br><br>
	 * where &beta;<sub>n</sub> is a coefficient determined through regression.
	 * 
	 * @param xs        The x data
	 * @param ys        The y data
	 * @param functions The {@link org.jtimer.Regression.Function functions} to use
	 */
	public FlexibleFunctionalFit(double[] xs, double[] ys, Collection<Function> functions) {
		this(xs, ys, functions.toArray(new Function[functions.size()]));
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
			result += coefficients[i] * (i == 0 ? 1 : functions[i - 1].calc(x));
		}
		return result;
	}
}
