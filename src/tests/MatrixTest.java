package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.jtimer.Exceptions.DimensionsInvalidException;
import org.jtimer.Regression.Matrix;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class MatrixTest {

	Random random = new Random();

	@DisplayName("Addition")
	@RepeatedTest(100)
	void addTest() {
		int one = random.nextInt(1000000);
		int two = random.nextInt(1000000);
		int three = random.nextInt(1000000);
		int four = random.nextInt(1000000);
		Matrix first = new Matrix(new Integer[][] { { one, two } });
		Matrix second = new Matrix(new Integer[][] { { three, four } });
		assertArrayEquals(new Double[][] { { (double) (one + three), (double) (two + four) } }, first.add(second).toArray());
	}

	@DisplayName("Addition Exception")
	@Test
	void invalidAddTest() {
		Matrix first = new Matrix(new Double[][] { { 3d, 1d, 2d }, { 2d, 1d, 3d } });
		Matrix second = new Matrix(new Double[][] { { 1d, 2d }, { 1d, 2d } });
		assertThrows(DimensionsInvalidException.class, () -> first.add(second));
	}

	@DisplayName("Subtraction")
	@RepeatedTest(100)
	void subtractTest() {
		int one = random.nextInt(1000000);
		int two = random.nextInt(1000000);
		int three = random.nextInt(1000000);
		int four = random.nextInt(1000000);
		Matrix first = new Matrix(new Integer[][] { { one, two } });
		Matrix second = new Matrix(new Integer[][] { { three, four } });
		assertArrayEquals(new Double[][] { { (double) (one - three), (double) (two - four) } }, first.subtract(second).toArray());
	}

	@DisplayName("Subtraction Exception")
	@Test
	void invalidSubtractTest() {
		Matrix first = new Matrix(new Double[][] { { 3d, 1d, 2d }, { 2d, 1d, 3d } });
		Matrix second = new Matrix(new Double[][] { { 1d, 2d }, { 1d, 2d } });
		assertThrows(DimensionsInvalidException.class, () -> first.subtract(second));
	}

	@DisplayName("Multiplication")
	@Test
	void multiplyTest() {
		Matrix first = new Matrix(new Double[][] { { 3d, 1d, 2d }, { 2d, 1d, 3d }, { 1d, 2d, 3d } });
		Matrix second = new Matrix(new Double[][] { { 1d, 2d }, { 2d, 1d }, { 5d, 10d } });
		assertArrayEquals(new Double[][] { { 15d, 27d }, { 19d, 35d }, { 20d, 34d } }, first.multiply(second).toArray());
	}

	@DisplayName("Multiplication Exception")
	@Test
	void invalidMultiplyTest() {
		Matrix first = new Matrix(new Double[][] { { 1d, 2d }, { 2d, 2d } });
		Matrix second = new Matrix(new Double[][] { { 1d, 2d, 3d }, { 1d, 2d, 3d }, { 3d, 1d, 3d } });
		assertThrows(DimensionsInvalidException.class, () -> first.multiply(second));
	}

	@DisplayName("Identity Multiplication")
	@RepeatedTest(100)
	void identityMultiplyTest() {
		Matrix first = new Matrix(new Double[][] { { 1d, 0d, 0d }, { 0d, 1d, 0d }, { 0d, 0d, 1d } });
		Matrix second = new Matrix(new Double[][] { { (double) random.nextInt(1000000), (double) random.nextInt(1000000), (double) random.nextInt(1000000) }, { (double) random.nextInt(1000000), (double) random.nextInt(1000000), (double) random.nextInt(1000000) }, { (double) random.nextInt(1000000), (double) random.nextInt(1000000), (double) random.nextInt(1000000) } });
		assertArrayEquals(second.multiply(first).toArray(), first.multiply(second).toArray());
	}

	@DisplayName("Scalar Multiplication")
	@RepeatedTest(100)
	void scalarMultiplyTest() {
		int one = random.nextInt(1000000);
		int two = random.nextInt(1000000);
		int scalar = random.nextInt(100);
		Matrix matrix = new Matrix(new Integer[][] { { one, two } });
		assertArrayEquals(new Double[][] { { (double) (one * scalar), (double) (two * scalar) } }, matrix.scalarMultiply((double) scalar).toArray());
	}

	@DisplayName("Transposition")
	@Test
	void transposeTest() {
		Matrix matrix = new Matrix(new Double[][] { { 1d, 2d, 3d }, { 3d, 2d, 1d } });
		assertArrayEquals(new Double[][] { { 1d, 3d }, { 2d, 2d }, { 3d, 1d } }, matrix.transpose().toArray());
	}

	@DisplayName("Inverse")
	@Test
	void inverseTest() {
		Matrix matrix = new Matrix(new Double[][] { { 1d, 2d }, { 3d, 4d } });
		assertArrayEquals(new Double[][] { { -2d, 1d }, { 1.5, -0.5 } }, matrix.inverse().toArray());
	}

	@DisplayName("Inverse Exception")
	@Test()
	void invalidInverseTest() {
		Matrix first = new Matrix(new Double[][] { { 1d, 2d, 3d }, { 2d, 2d, 3d } });
		assertThrows(DimensionsInvalidException.class, () -> first.inverse());
	}

	@DisplayName("Determinant")
	@Test
	void determinantTest() {
		Matrix matrix = new Matrix(new Integer[][] { { 5 } });
		assertEquals(new Double(5), matrix.determinant());
		matrix = new Matrix(new Float[][] { { 1f, 2f }, { 3f, 4f } });
		assertEquals(new Double(-2), matrix.determinant());
		matrix = new Matrix(new Double[][] { { 1d, 2d, 3d, 4d }, { 5d, 6d, 7d, 8d }, { 9d, 10d, 11d, 12d }, { 13d, 14d, 15d, 16d } });
		assertEquals(new Double(0), matrix.determinant());
		matrix = new Matrix(new Double[][] { { 1d, 0d, 0d, 0d }, { 0d, 1d, 0d, 0d }, { 0d, 0d, 1d, 0d }, { 0d, 0d, 0d, 1d } });
		assertEquals(new Double(1), matrix.determinant());
		matrix = new Matrix(new Double[][] { { 1d, 0d, 0d, 0d }, { 0d, 2d, 0d, 0d }, { 0d, 0d, 3d, 0d }, { 0d, 0d, 0d, 4d } });
		assertEquals(new Double(24), matrix.determinant());
		matrix = new Matrix(new Double[][] { { 7d, 2d, 3d }, { -5d, -7d, -7d }, { 8d / 5d, -18d, 11d } });
		assertEquals(-5149d / 5d, matrix.determinant().doubleValue(), 0.001);
	}

	@DisplayName("Random Determinant")
	@RepeatedTest(100)
	void randomDeterminantTest() {
		int one = random.nextInt(10000) - 5000;
		int two = random.nextInt(10000) - 5000;
		int three = random.nextInt(10000) - 5000;
		int four = random.nextInt(10000) - 5000;
		Matrix matrix = new Matrix(new Integer[][] { { one, two }, { three, four } });
		assertEquals(new Double(one * four - two * three), matrix.determinant());
	}
}
