import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.jtimer.Regression.FlexibleFunctionalFit;
import org.jtimer.Regression.Function;
import org.jtimer.Regression.FunctionalFit;
import org.jtimer.Regression.PolynomialFit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegressionTest {

	/**
	 * f(x) =
	 * &beta;<sub>1</sub>+&beta;<sub>2</sub><b>&middot;</b>(2.5<b>&middot;</b>x<sup>3</sup>+e<sup>x</sup>)
	 */
	@DisplayName("Exact Functional Fit")
	@Test
	void perfectFuncFitTest() {
		double[] xs = new double[] { 0, 1, 2 };
		double[] ys = new double[] { 2, 6.218281828459045, 28.389056098930652 };
		FunctionalFit fit = new FunctionalFit(xs, ys, x -> 2.5 * Math.pow(x, 3) + Math.pow(Math.E, x));
		assertEquals(461.913, fit.calculate(5), 0.001);
		assertEquals(0, fit.error(xs, ys), 0.001);
	}

	/**
	 * f(x) =
	 * &beta;<sub>1</sub>+&beta;<sub>2</sub><b>&middot;</b>(x!+e<sup>x</sup>+x<sup>x</sup><b>&middot;</b>-1<sup>x</sup>)+&beta;<sub>3</sub><b>&middot;</b>(x!+e<sup>x</sup>+x<sup>x</sup><b>&middot;</b>-1<sup>x</sup>)<sup>2</sup>
	 */
	@DisplayName("Pre-Determined Functional Fit")
	@Test
	void funFuncFitTest() {
		double[] xs = new double[] { 1, 2, 3, 4, 5 };
		double[] ys = new double[] { 1, -1, 5, -5, 6 };
		FunctionalFit fit = new FunctionalFit(xs, ys, x -> factorial(x) + Math.pow(Math.E, x) + Math.pow(x, x) * Math.pow(-1, x), 3);
		double[] testValues = new double[] { 1, 6, 3, 4, 2 };
		double[] expectedValues = new double[] { 1.74515, -14587.9, 1.81282, -5.10482, 1.54548 }; // Calculated using Mathematica
		for (int i = 0; i < testValues.length; i++) {
			assertEquals(expectedValues[i], fit.calculate(testValues[i]), 0.05);
		}
		assertEquals(4.15, fit.error(xs, ys), 0.05);
	}

	/**
	 * f(x) =
	 * &beta;<sub>1</sub>+&beta;<sub>2</sub>e<sup>x</sup>+&beta;<sub>3</sub>x<sup>3</sup>
	 */
	@DisplayName("Flexible Functional Fit")
	@Test
	void flexArrFuncFitTest() {
		double[] xs = new double[] { 1, 2, 5, 6 };
		double[] ys = new double[] { 1, 4, 1, 7 };
		FlexibleFunctionalFit fit = new FlexibleFunctionalFit(xs, ys, x -> Math.pow(Math.E, x), x -> Math.pow(x, 3));
		double[] testValues = new double[] { 1, 2, 5, 6, 3, -2 };
		double[] expectedValues = new double[] { 2.5588, 2.31652, 1.16508, 6.9596, 1.65977, 3.02784 }; // Calculated using Mathematica
		for (int i = 0; i < testValues.length; i++) {
			assertEquals(expectedValues[i], fit.calculate(testValues[i]), 0.05);
		}
		assertEquals(2.30, fit.error(xs, ys), 0.05);
	}

	/**
	 * f(x) =
	 * &beta;<sub>1</sub>+&beta;<sub>2</sub>x!+&beta;<sub>3</sub>e<sup>sin(x)</sup>+&beta;<sub>4</sub>tan<sup>-1</sup>(x<sup>4</sup>)
	 */
	@DisplayName("Flexible Functional Fit")
	@Test
	void flexColFuncFitTest() {
		double[] xs = new double[] { 1, 3, 4, 6 };
		double[] ys = new double[] { 83, 13, 61, 32 };
		List<Function> functions = Arrays.asList(new Function[] { x -> factorial(x), x -> Math.pow(Math.E, Math.sin(x)), x -> Math.atan(Math.pow(x, 4)) });
		FlexibleFunctionalFit fit = new FlexibleFunctionalFit(xs, ys, functions);
		double[] testValues = new double[] { 1, 2, 5, 7, 3 };
		double[] expectedValues = new double[] { 83, -74.1989, 65.7948, -99.6394, 13 }; // Calculated using Mathematica
		for (int i = 0; i < testValues.length; i++) {
			assertEquals(expectedValues[i], fit.calculate(testValues[i]), 0.05);
		}
		assertEquals(0, fit.error(xs, ys), 0.001);
	}

	@DisplayName("Exact Polynomial Fit")
	@Test
	void perfectPolyFitTest() {
		double[] xs = new double[] { 1, 2, 3, 4, 5 };
		double[] ys = new double[] { 5, 2, 8, 9, 30 };
		PolynomialFit fit = new PolynomialFit(xs, ys);
		for (int i = 0; i < xs.length; i++) {
			assertEquals(ys[i], fit.calculate(xs[i]), 0.001);
		}
		assertEquals(0, fit.error(xs, ys), 0.001);
	}

	@DisplayName("Pre-Determined Polynomial Fit")
	@Test
	void semiPolyFitTest() {
		double[] xs = new double[] { 1, 2, 3, 4, 5 };
		double[] ys = new double[] { 5, 2, 8, 9, 30 };
		double[] expectedys = new double[] { 4.44286, 4.22857, 4.65714, 11.2286, 29.4429 };
		PolynomialFit fit = new PolynomialFit(xs, ys, 4);
		for (int i = 0; i < xs.length; i++) {
			assertEquals(expectedys[i], fit.calculate(xs[i]), 0.001);
		}
		assertEquals(4.66, fit.error(xs, ys), 0.05);
	}

	private final double factorial(double x) {
		if (x == 0) {
			return 1;
		} else {
			return x * factorial(x - 1);
		}
	}
}
