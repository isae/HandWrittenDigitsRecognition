package ru.ifmo.ctddev.isaev.data;

/**
 * @author iisaev
 */
public interface NeuralNetwork {
    void train(double[] input, int expectedOutput);

    int predict(double[] input);
}
