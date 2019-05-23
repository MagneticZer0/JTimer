package org.jtimer.Regression;

/**
 * A custom function interface to define the function to use.
 */
public interface Function {
	/**
	 * Takes in an X value, applies a function to it and returns the output
	 * 
	 * @param x The x value to apply the function to
	 * @return f(x), the function evaluated at x.
	 */
	public double calc(double x);
}