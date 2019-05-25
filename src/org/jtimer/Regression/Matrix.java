package org.jtimer.Regression;

import java.util.Arrays;

import org.jtimer.Exceptions.DimensionsInvalidException;

/**
 * A custom matrix class I use to make things easier.
 * 
 * @author MagneticZero
 */
public class Matrix {

	/**
	 * The internal data structure.
	 */
	private Double[][] matrix;

	/**
	 * Creates an {@link org.jtimer.Regression.Matrix matrix} of size <i>i</i> x
	 * <i>j</i> and sets all the elements to 0.
	 * 
	 * @param i The number of rows
	 * @param j The number of columns
	 */
	public Matrix(int i, int j) {
		matrix = new Double[i][j];
		for (int iX = 0; iX < i; iX++) {
			for (int jY = 0; jY < j; jY++) {
				matrix[iX][jY] = 0d;
			}
		}
	}

	/**
	 * Creates a {@link org.jtimer.Regression.Matrix matrix} using a specified
	 * double[][].
	 * 
	 * @param matrix The double[][] to transform into a matrix
	 */
	public Matrix(Number[][] matrix) {
		this(matrix.length, matrix[0].length);
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				this.matrix[i][j] = matrix[i][j].doubleValue();
			}
		}
	}

	/**
	 * Adds 2 {@link org.jtimer.Regression.Matrix matrices} and returns the result
	 * as a {@link org.jtimer.Regression.Matrix matrix}.
	 * 
	 * @param other The {@link org.jtimer.Regression.Matrix matrix} to add
	 * @return The result
	 * @throws DimensionsInvalidException If the 2
	 *                                    {@link org.jtimer.Regression.Matrix
	 *                                    matrices} do not have the same dimensions
	 */
	public Matrix add(Matrix other) throws DimensionsInvalidException {
		if (matrix.length != other.matrix.length || matrix[0].length != other.matrix[0].length) {
			throw new DimensionsInvalidException();
		}
		Double[][] result = new Double[matrix.length][other.matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				result[i][j] = matrix[i][j] + other.matrix[i][j];
			}
		}
		return new Matrix(result);
	}

	/**
	 * Subtracts 2 {@link org.jtimer.Regression.Matrix matrices} and returns the
	 * result as a {@link org.jtimer.Regression.Matrix matrix}.
	 * 
	 * @param other The {@link org.jtimer.Regression.Matrix matrix} to add
	 * @return The result
	 * @throws DimensionsInvalidException If the 2
	 *                                    {@link org.jtimer.Regression.Matrix
	 *                                    matrices} do not have the same dimensions
	 */
	public Matrix subtract(Matrix other) throws DimensionsInvalidException {
		if (matrix.length != other.matrix.length || matrix[0].length != other.matrix[0].length) {
			throw new DimensionsInvalidException();
		}
		Double[][] result = new Double[matrix.length][other.matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				result[i][j] = matrix[i][j] - other.matrix[i][j];
			}
		}
		return new Matrix(result);
	}

	/**
	 * Multiplies a {@link org.jtimer.Regression.Matrix matrix} by a scalar as
	 * returns the result as a {@link org.jtimer.Regression.Matrix matrix}.
	 * 
	 * @param other The scalar to multiply by
	 * @return The result
	 */
	public Matrix scalarMultiply(Double other) {
		Double[][] result = new Matrix(matrix.length, matrix[0].length).toArray();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				result[i][j] = other * matrix[i][j];
			}
		}
		return new Matrix(result);
	}

	/**
	 * Multiplies 2 {@link org.jtimer.Regression.Matrix matrices} and returns the
	 * result as a {@link org.jtimer.Regression.Matrix matrix}.
	 * <br>
	 * Note: In order to multiply 2 {@link org.jtimer.Regression.Matrix matrices}
	 * the sizes need to be <i>i</i> x <i>j</i> and <i>j</i> x <i>f</i>. The result
	 * will be an <i>i</i> x <i>f</i> matrix.
	 * 
	 * @param other The {@link org.jtimer.Regression.Matrix matrix} to multiply
	 * @return The result
	 * @throws DimensionsInvalidException If the 2
	 *                                    {@link org.jtimer.Regression.Matrix
	 *                                    matrices} do not have compatible
	 *                                    dimensions
	 */
	public Matrix multiply(Matrix other) throws DimensionsInvalidException {
		if (this.matrix[0].length != other.matrix.length) {
			throw new DimensionsInvalidException();
		} else {
			Double[][] result = new Matrix(matrix.length, other.matrix[0].length).toArray();
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < other.matrix[0].length; j++) {
					for (int k = 0; k < matrix[0].length; k++) {
						result[i][j] += matrix[i][k] * other.matrix[k][j];
					}
				}
			}
			return new Matrix(result);
		}
	}

	/**
	 * Transposes a {@link org.jtimer.Regression.Matrix matrix} and returns the
	 * result as a {@link org.jtimer.Regression.Matrix matrix}.
	 * 
	 * @return The transposed matrix
	 */
	public Matrix transpose() {
		Double[][] result = new Matrix(matrix[0].length, matrix.length).toArray();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				result[j][i] = matrix[i][j];
			}
		}
		return new Matrix(result);
	}

	/**
	 * Inverts a {@link org.jtimer.Regression.Matrix matrix} and returns the result
	 * as a {@link org.jtimer.Regression.Matrix matrix}.
	 * 
	 * @return The inverted matrix
	 * @throws DimensionsInvalidException If the {@link org.jtimer.Regression.Matrix
	 *                                    matrix} is not a square
	 *                                    {@link org.jtimer.Regression.Matrix
	 *                                    matrix}
	 */
	public Matrix inverse() throws DimensionsInvalidException {
		if (matrix.length != matrix[0].length) {
			throw new DimensionsInvalidException();
		} else {
			Double[][] result = new Matrix(matrix.length, matrix.length).toArray();
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix.length; j++) {
					result[i][j] = Math.pow(-1, i + j) * minorMatrix(i, j).determinant();
				}
			}
			double num = 1.0 / determinant();
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j <= i; j++) {
					double temp = result[i][j];
					result[i][j] = result[j][i] * num;
					result[j][i] = temp * num;
				}
			}
			return new Matrix(result);
		}
	}

	/**
	 * Calculates the determinant of the {@link org.jtimer.Regression.Matrix matrix}
	 * and returns it.
	 * 
	 * @return The determinant
	 * @throws DimensionsInvalidException If the {@link org.jtimer.Regression.Matrix
	 *                                    matrix} is not a square
	 *                                    {@link org.jtimer.Regression.Matrix
	 *                                    matrix}
	 */
	public Double determinant() {
		if (matrix.length != matrix[0].length) {
			throw new DimensionsInvalidException();
		} else {
			if (matrix.length == 1) {
				return matrix[0][0];
			} else {
				Double determinant = 0d;
				for (int i = 0; i < matrix.length; i++) {
					determinant += Math.pow(-1, i) * matrix[0][i] * minorMatrix(0, i).determinant();
				}
				return determinant;
			}
		}
	}

	/**
	 * Used to get a smaller {@link org.jtimer.Regression.Matrix sub-matrix} by
	 * eliminating a row and column number.
	 * 
	 * @param row The row to exclude
	 * @param col The column to exlude
	 * @return The submatrix
	 */
	public Matrix minorMatrix(int row, int col) {
		Double[][] result = new Matrix(matrix.length - 1, matrix.length - 1).toArray();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; i != row && j < matrix.length; j++) {
				if (j != col) {
					result[i < row ? i : i - 1][j < col ? j : j - 1] = matrix[i][j];
				}
			}
		}
		return new Matrix(result);
	}

	/**
	 * Creates an identity {@link org.jtimer.Regression.Matrix matrix} that is
	 * <i>size</i> x <i>size</i> large.
	 * <br>
	 * The identity has the property that <b>A</b><sub><i>n</i> x <i>n</i></sub> x
	 * <b>I</b><sub><i>n</i> x <i>n</i></sub> = <b>A</b><sub><i>n</i> x
	 * <i>n</i></sub>
	 * 
	 * @param size The size of the identity matrix
	 * @return Returns the newly created identity matrix.
	 */
	public static Matrix identity(int size) {
		Double[][] result = new Matrix(size, size).toArray();
		for (int i = 0; i < result.length; i++) {
			result[i][i] = 1d;
		}
		return new Matrix(result);
	}

	/**
	 * Returns the string representation of the {@link org.jtimer.Regression.Matrix
	 * matrix}.
	 */
	public String toString() {
		StringBuffer string = new StringBuffer();
		for (Double[] arr : matrix) {
			string.append(Arrays.toString(arr) + "\n");
		}
		return string.toString();
	}

	/**
	 * Returns the internal array that the {@link org.jtimer.Regression.Matrix
	 * matrix} is using.
	 * 
	 * @return The Double[][] representing the {@link org.jtimer.Regression.Matrix
	 *         matrix}
	 */
	public Double[][] toArray() {
		return matrix.clone();
	}
}
