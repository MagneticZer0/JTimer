package org.jtimer.Exceptions;

/**
 * Used if an {@link org.jtimer.Readability.If If} object's
 * {@link org.jtimer.Readability.If#Else(Object) Else} method is executed before
 * the {@link org.jtimer.Readability.If#Then(Object) Then} method.
 * 
 * @author MagneticZero
 *
 */
public class NotProperlyInitializedException extends RuntimeException {

	private static final long serialVersionUID = -1923285635403639187L;

}
