package org.jtimer.Regression;

import java.util.Arrays;

import org.jtimer.Exceptions.DimensionsInvalidException;

public class Matrix {

    private Double[][] matrix;

    public Matrix(int i, int j) {
        matrix = new Double[i][j];
        for (int iX = 0; iX < i; iX++) {
            for (int jY = 0; jY < j; jY++) {
                matrix[iX][jY] = 0d;
            }
        }
    }

    public Matrix(Number[][] matrix) {
        this(matrix.length, matrix[0].length);
        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix[0].length; j++) {
                this.matrix[i][j] = matrix[i][j].doubleValue();
            }
        }
    }

    public Matrix add(Matrix other) {
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

    public Matrix subtract(Matrix other) {
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

    public Matrix scalarMultiply(Double other) {
        Double[][] result = new Matrix(matrix.length, matrix[0].length).toArray();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[i][j] = other * matrix[i][j];
            }
        }
        return new Matrix(result);
    }

    public Matrix multiply(Matrix other) {
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

    public Matrix transpose() {
        Double[][] result = new Matrix(matrix[0].length, matrix.length).toArray();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return new Matrix(result);
    }
    
    public Matrix inverse() {
    	Double[][] result = new Matrix(matrix.length, matrix.length).toArray();
    	for(int i=0; i<matrix.length; i++) {
    		for(int j=0; j<matrix.length; j++) {
    			result[i][j] = Math.pow(-1, i+j)*minorMatrix(i, j).determinant();
    		}
    	}
    	double num = 1.0 / determinant();
    	for(int i=0; i<matrix.length; i++) {
    		for(int j=0; j<=i; j++) {
    			double temp = result[i][j];
    			result[i][j] = result[j][i]*num;
    			result[j][i] = temp*num;
    		}
    	}
    	return new Matrix(result);
    }

    public Double determinant() {
        if (matrix.length == matrix[0].length) {
            if (matrix.length == 1) {
                return matrix[0][0];
            } else {
                Double determinant = 0d;
                for (int i = 0; i < matrix.length; i++) {
                    determinant += Math.pow(-1, i) * matrix[0][i] * coFactor(i).determinant();
                }
                return determinant;
            }
        } else {
            throw new DimensionsInvalidException();
        }
    }

    public Matrix coFactor(int c) {
        Double[][] coFactor = new Double[matrix.length - 1][matrix.length - 1];
        int i = 0, j = 0;
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                if (row != 0 && col != c) {
                    coFactor[i][j++] = matrix[row][col];

                    if (j == matrix.length - 1) {
                        j = 0;
                        i++;
                    }
                }
            }
        }
        return new Matrix(coFactor);
    }
    
    public Matrix minorMatrix(int row, int col) {
    	Double[][] result = new Matrix(matrix.length-1, matrix.length-1).toArray();
    	for (int i=0; i<matrix.length; i++) {
    		for (int j=0; i != row && j<matrix.length; j++) {
    			if (j!= col) {
    				result[i < row ? i : i-1][j < col ? j : j-1] = matrix[i][j];
    			}
    		}
    	} 
    	return new Matrix(result);
    }

    public String toString() {
        StringBuffer string = new StringBuffer();
        for (Double[] arr : matrix) {
            string.append(Arrays.toString(arr) + "\n");
        }
        return string.toString();
    }

    public Double[][] toArray() {
        return matrix.clone();
    }
}
