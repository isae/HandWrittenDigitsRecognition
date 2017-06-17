package ru.ifmo.ctddev.isaev.data;

import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;


/**
 * @author iisaev
 */
@SuppressWarnings("SimplifyStreamApiCallChains")
public class Matrix {

    private static final double EPSILON_INIT = 0.12;

    private static final Random RANDOM = new Random();

    @NotNull
    private final double[][] data;

    private final int rowCount;

    private final int columnCount;

    public Matrix(double[][] data) {
        this.data = data;
        this.rowCount = data.length;
        if (rowCount == 0) {
            throw new IllegalStateException("Matrix cannot be zero-height");
        }
        this.columnCount = data[0].length;
        if (columnCount == 0) {
            throw new IllegalStateException("Matrix cannot be zero-width");
        }
        if (!stream(data).anyMatch(arr -> arr.length == columnCount)) {
            throw new IllegalStateException("Invalid matrix column");
        }
    }

    public Matrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.data = new double[rowCount][columnCount];
    }

    public static Matrix fromColumn(double[] column) {
        Matrix result = new Matrix(column.length, 1);
        for (int i = 0; i < column.length; i++) {
            result.data[i][0] = column[i];
        }
        return result;
    }

    public static Matrix fromRow(double[] column) {
        return new Matrix(new double[][] {column});
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public double[][] getData() {
        return data;
    }

    public Matrix apply(Function<Double, Double> function) {
        throw new UnsupportedOperationException();
    }

    public Matrix fillRandom() {
        Matrix result = new Matrix(data);
        range(0, rowCount)
                .forEach(i -> range(0, columnCount)
                        .forEach(j -> {
                            result.data[i][j] = RANDOM.nextDouble() * 2 * EPSILON_INIT - EPSILON_INIT;
                        }));
        return result;
    }
}
