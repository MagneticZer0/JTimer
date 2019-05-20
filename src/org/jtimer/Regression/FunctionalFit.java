package org.jtimer.Regression;

/**
 * Used to fit a set of data to a certain function.
 * 
 * @author MagneticZero
 */
public class FunctionalFit extends Regression {

	/**
	 * The coefficients of the regression.
	 */
	double[] coefficients;
	/**
	 * The function used.
	 */
	Function function;

	/**
	 * Creates a FunctionalFit that is in the form f(x) = function(x)+C.
	 * 
	 * @param xs       The x data
	 * @param ys       The y data
	 * @param function The function to use
	 */
	public FunctionalFit(double[] xs, double[] ys, Function function) {
		this(xs, ys, function, 2);
	}

	/**
	 * Creates a higher order FunctionalFit that is in the form of f(x) =
	 * C+function(x) + ... + function(x)<sup>degree-1</sup>.
	 * 
	 * @param xs       The x data
	 * @param ys       The y data
	 * @param function The function to use
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

	/**
	 * A custom function interface to define the function to use.
	 */
	public interface Function {
		public double calc(double x);
	}

}
