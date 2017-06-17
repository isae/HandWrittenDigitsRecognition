package ru.ifmo.ctddev.isaev.data;

import static java.lang.String.format;


/**
 * @author iisaev
 */
public class NeuralNetworkImpl implements NeuralNetwork {
    private final int inputLayerSize;

    private final int hiddenLayerSize;

    private final int outputLayerSize;

    private final Matrix theta1; // network params between input layer and hidden layer

    private final Matrix theta2; // network params between hidden layer and output layer

    public NeuralNetworkImpl(int inputLayerSize, int hiddenLayerSize, int outputLayerSize) {
        this.inputLayerSize = inputLayerSize;
        this.hiddenLayerSize = hiddenLayerSize;
        this.outputLayerSize = outputLayerSize;
        this.theta1 = new Matrix(hiddenLayerSize, inputLayerSize + 1).fillRandom(); // +1 for bias term
        this.theta2 = new Matrix(outputLayerSize, hiddenLayerSize + 1).fillRandom();
    }

    @Override
    public void train(double[] input, int expectedOutput) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int predict(double[] example) {
        Matrix a1 = Matrix.fromColumn(example)
                .prependWithRowOfOne();
        Matrix a2 = a1.multiply(theta1).apply(this::sigmoidValue)
                .prependWithRowOfOne();
        Matrix a3 = a2.multiply(theta2).apply(this::sigmoidValue);
        if (a3.getRowCount() != outputLayerSize || a3.getColumnCount() != 1) {
            throw new IllegalStateException(
                    format("Invalid result array size: %sx%s", a3.getRowCount(), a3.getColumnCount())
            );
        }
        double[] result = new double[a3.getRowCount()];
        for (int i = 0; i < a3.getRowCount(); ++i) {
            result[i] = a3.getData()[i][0];
        }
        return indMax(result);
    }


    private double sigmoidValue(double arg) {
        return 1 / (1 + Math.exp((-arg)));
    }

    private int indMax(double[] arr) {
        double max = Double.MIN_VALUE;
        int result = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                result = i;
            }
        }
        return result;
    }
}
