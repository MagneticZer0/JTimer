package org.jtimer.Regression;

/**
 * Used to fit a set of data to polynomial
 * 
 * @author MagneticZero
 */
public class PolynomialFit extends LinearRegression {

	/**
	 * The coefficients of the regression
	 */
	double[] coefficients;

	/**
	 * Creates a PolynomialFit that will over-fit the data provided
	 * 
	 * @param xs The x data
	 * @param ys The y data
	 */
	public PolynomialFit(double[] xs, double[] ys) {
		this(xs, ys, xs.length);
	}

	/**
	 * Creates a higher order PolynomialFit that is in the form of f(x) = C+x + ...
	 * + x^degree
	 * 
	 * @param xs    The x data
	 * @param ys    The y data
	 * @param terms The degree to use
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
	 * Calculates f(x) using the generated linear regression
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
