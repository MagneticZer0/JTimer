package org.jtimer.Regression;

public class PolynomialFit extends LinearRegression {
	
	double[] coefficients;
	
	public PolynomialFit(double[] xs, double[] ys) {
		this(xs, ys, xs.length);
	}
	
	public PolynomialFit(double[] xs, double[] ys, int terms) {
		coefficients = new double[terms];
		Matrix xsMatrix = new Matrix(xs.length, terms);
		for(int i=0; i<xs.length; i++) {
			for(int j=0; j<terms; j++) {
				xsMatrix.toArray()[i][j] = Math.pow(xs[i], j);
			}
		}
		Matrix ysMatrix = new Matrix(ys.length, 1);
		for(int i=0; i<ys.length; i++) {
			ysMatrix.toArray()[i][0] = ys[i];
		}
		Matrix AtA = xsMatrix.transpose().multiply(xsMatrix);
		Matrix Atx = xsMatrix.transpose().multiply(ysMatrix);
		Matrix solutionMatrix = AtA.inverse().multiply(Atx);
		for(int i=0; i<terms; i++) {
			coefficients[i] = solutionMatrix.toArray()[i][0];
		}
		error(xs, ys);
	}
	
	@Override
	public double calculate(double x) {
		double result = 0;
		for(int i=0; i<coefficients.length; i++) {
			result += coefficients[i]*Math.pow(x, i);
		}
		return result;
	}
}
