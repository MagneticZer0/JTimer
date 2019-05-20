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