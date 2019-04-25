package org.jtimer.Regression;

/**
 * An abstract class that acts as a base-line for the
 * other forms of linear regression. Has methods to compare
 * regressions, calculate the error, and setting the names and
 * an abstract method for calculating f(x).
 */
public abstract class LinearRegression implements Comparable<LinearRegression> {
	
	private Double error = Double.NaN; // The error for the regression
	private String name; // The name for the regression
	
	/**
	 * Calculates the y for the given x using
	 * the specified method
	 * @param x The x to input
	 * @return f(x) aka y
	 */
	public abstract double calculate(double x);
	
	/**
	 * Calculates the error through a pair of
	 * known xs and ys.
	 * @param xs The x array
	 * @param ys The y array
	 * @return The error generated
	 */
	public double error(double[] xs, double[] ys) {
		if (error.isNaN()) {
			double dist = 0;
			for(int i=0; i<xs.length; i++) {
				dist += Math.pow(ys[i]-calculate(xs[i]), 2);
			}
			error = Math.sqrt(dist);
		}
		return error;
	}
	
	/**
	 * Comapres one regression to another by using the error
	 */
	public int compareTo(LinearRegression reg) {
		return (int) Math.round((error-reg.error)*100);
	}
	
	/**
	 * A way to get the name
	 */
	public String toString() {
		return name;
	}
	
	/**
	 * Sets the name of the LinearRegression, this value is returned
	 * by {@link LinearRegression#toString()}.
	 * @param name The name to set to
	 * @return Just a convenience
	 */
	public LinearRegression name(String name) {
		this.name = name;
		return this;
	}
}
