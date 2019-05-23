package org.jtimer.Regression;

import java.util.Collection;

/**
 * Used to fit a set of data to a certain set function of functions.
 * 
 * @author MagneticZero
 */
public class FlexibleFunctionalFit extends Regression {

	/**
	 * The coefficients of the regression.
	 */
	double[] coefficients;
	/**
	 * The function used.
	 */
	Function[] functions;

	/**
	 * Creates a FlexibleFunctionalFit that is in the form f(x) =
	 * C+f<sub>1</sub>(x)+f<sub>2</sub>(x)... using an array of functions
	 * 
	 * @param xs       The x data
	 * @param ys       The y data
	 * @param function The function to use
	 */
	public FlexibleFunctionalFit(double[] xs, double[] ys, Function... functions) {
		this.functions = functions;
		coefficients = new double[functions.length+1];
		Matrix xsMatrix = new Matrix(xs.length, functions.length+1);
		for (int i = 0; i < xs.length; i++) {
			for (int j = 0; j <= functions.length; j++) {
				xsMatrix.toArray()[i][j] = j == 0 ? 1 : functions[j-1].calc(xs[i]);
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
	 * Creates a FlexibleFunctionalFit that is in the form f(x) =
	 * C+f<sub>1</sub>(x)+f<sub>2</sub>(x)... using a collection of functions
	 * 
	 * @param xs        The x data
	 * @param ys        The y data
	 * @param functions The function to use
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
			result += coefficients[i] * (i == 0 ? 1 : functions[i-1].calc(x));
		}
		return result;
	}
}
