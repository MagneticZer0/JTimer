package org.jtimer.Readability;

import org.jtimer.Exceptions.NotProperlyInitializedException;

/**
 * This is a fancy ternary operator just used for code-readability
 * 
 * @author MagneticZero
 *
 * @param <V> The return value type
 */
public class If<V> {

	/**
	 * The conditional that will be checked
	 */
	private boolean conditional;
	/**
	 * If the conditional is true, this value will be returned.
	 */
	private V thenValue;
	/**
	 * If the {@link org.jtimer.Readability.If#Then(Object)} has been executed.
	 */
	private boolean thenExecuted = false;
	/**
	 * If the conditional is false, this value will be returned.
	 */
	private V elseValue;

	/**
	 * Intialize the If object with the initial boolean
	 * 
	 * @param conditional The boolean to test
	 */
	public If(boolean conditional) {
		this.conditional = conditional;
	}

	/**
	 * This is used to set the value that is returned if the boolean is true
	 * 
	 * @param thenValue The value to return if true
	 * @return Returns the If object for convenience
	 */
	public If<V> Then(V thenValue) {
		this.thenValue = thenValue;
		thenExecuted = true;
		return this;
	}

	/**
	 * This is used to set the value that is returned if the boolean is false. This
	 * will also evaluate the expression and return the correct value
	 * 
	 * @param elseValue The value to return if false
	 * @return Returns the proper value
	 * @throws NotProperlyInitializedException If {@link org.jtimer.Readability.If#Then(Object)} was not
	 *                                         used before this.
	 */
	public V Else(V elseValue) throws NotProperlyInitializedException {
		this.elseValue = elseValue;
		return exec();
	}

	/**
	 * Executes the If object, this is a private method that gets executed right
	 * after the else method is used.
	 * 
	 * @return Returns the proper value
	 * @throws NotProperlyInitializedException If {@link org.jtimer.Readability.If#Then(Object)} was not
	 *                                         used before this.
	 */
	private V exec() throws NotProperlyInitializedException {
		if (!thenExecuted) {
			throw new NotProperlyInitializedException();
		}
		return conditional ? thenValue : elseValue;
	}

}
