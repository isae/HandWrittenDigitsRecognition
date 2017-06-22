package ru.ifmo.ctddev.isaev.algorithm

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

abstract class NeuralNetwork {

    abstract fun getTheta1(): Matrix
    // network params between input layer and hidden layer

    abstract fun getTheta2(): Matrix
    // network params between hidden layer and output layer

    val lambda = 0.0

    val gradientSteps = 50

    fun predictResult(example: DoubleArray): Int {
        val a3 = predict(example, getTheta1(), getTheta2())
        val result = DoubleArray(a3.rowCount)
        for (i in 0..a3.rowCount - 1) {
            result[i] = a3.getAt(i, 0)
        }
        return indMax(result)
    }

}

class NewNetwork(private val inputLayerSize: Int,
                 private val hiddenLayerSize: Int,
                 private val numLabels: Int,
                 trainingData: List<TrainObject>,
                 storeDataset: Boolean) : NeuralNetwork() {
    override fun getTheta1(): Matrix {
        return theta1
    }

    override fun getTheta2(): Matrix {
        return theta2
    }

    private var theta1: Matrix

    private var theta2: Matrix

    init {
        val initTheta1 = randomMatrix(hiddenLayerSize, inputLayerSize + 1)
        val initTheta2 = randomMatrix(numLabels, hiddenLayerSize + 1)
        println("Started training data initialization")
        val dataset = Matrix(trainingData.map { it.data }.toTypedArray())
        val y = trainingData.map { it.clazz }.toTypedArray()
        println("Finished training data initialization")
        val costFunction = { params: DoubleArray ->
            nnCostFunction(params, inputLayerSize, hiddenLayerSize, numLabels, dataset.copy(), y, lambda)
        }
        println("Started optimization process")
        val X = pack(initTheta1, initTheta2)
        val (newX, fX, i) = fmincg(costFunction, X, gradientSteps)

        if (storeDataset) {
            PrintWriter(
                    FileWriter("./resources/nnParams${System.currentTimeMillis()}")
            ).use { writer ->
                newX.forEach {
                    writer.println(it)
                }
            }
        }
        theta1 = reshape(newX.subArray(
                0, hiddenLayerSize * (inputLayerSize + 1)),
                hiddenLayerSize, (inputLayerSize + 1)
        )

        theta2 = reshape(newX.subArray(
                (hiddenLayerSize * (inputLayerSize + 1)), newX.size),
                numLabels, (hiddenLayerSize + 1)
        )
    }
}

class PretrainedNetwork(inputLayerSize: Int,
                        hiddenLayerSize: Int,
                        numLabels: Int,
                        datasetSuffix: String) : NeuralNetwork() {
    override fun getTheta1(): Matrix {
        return theta1
    }

    override fun getTheta2(): Matrix {
        return theta2
    }

    private var theta1: Matrix

    private var theta2: Matrix

    init {
        val newX = File("./resources/nnParams$datasetSuffix")
                .readLines()
                .map { it.toDouble() }
                .toDoubleArray()


        theta1 = reshape(newX.subArray(
                0, hiddenLayerSize * (inputLayerSize + 1)),
                hiddenLayerSize, (inputLayerSize + 1)
        )

        theta2 = reshape(newX.subArray(
                (hiddenLayerSize * (inputLayerSize + 1)), newX.size),
                numLabels, (hiddenLayerSize + 1)
        )
    }
}