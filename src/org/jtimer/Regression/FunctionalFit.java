package org.jtimer.Regression;

public class FunctionalFit extends LinearRegression {
	
	double[] coefficients;
	Function function;
	
	public FunctionalFit(double[] xs, double[] ys, Function function) {
		this(xs, ys, function, 2);
	}
	
	public FunctionalFit(double[] xs, double[] ys, Function function, int degree) {
		this.function = function;
		coefficients = new double[degree];
		Matrix xsMatrix = new Matrix(xs.length, degree);
		for(int i=0; i<xs.length; i++) {
			for(int j=0; j<degree; j++) {
				xsMatrix.toArray()[i][j] = Math.pow(function.calc(xs[i]), j);
			}
		}
		Matrix ysMatrix = new Matrix(ys.length, 1);
		for(int i=0; i<ys.length; i++) {
			ysMatrix.toArray()[i][0] = ys[i];
		}
		Matrix AtA = xsMatrix.transpose().multiply(xsMatrix);
		Matrix Atx = xsMatrix.transpose().multiply(ysMatrix);
		Matrix solutionMatrix = AtA.inverse().multiply(Atx);
		for(int i=0; i<degree; i++) {
			coefficients[i] = solutionMatrix.toArray()[i][0];
		}
		error(xs, ys);
	}
	
	@Override
	public double calculate(double x) {
		double result = 0;
		for(int i=0; i<coefficients.length; i++) {
			result += coefficients[i]*Math.pow(function.calc(x), i);
		}
		return result;
	}
	
	public interface Function {
		public double calc(double x);
	}

}
