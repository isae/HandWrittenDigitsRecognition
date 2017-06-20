package ru.ifmo.ctddev.isaev.algorithm

class NeuralNetwork(private val inputLayerSize: Int,
                    private val hiddenLayerSize: Int,
                    private val outputLayerSize: Int,
                    private val trainingData: List<TrainObject>) {

    private val theta1: Matrix = randomMatrix(hiddenLayerSize, inputLayerSize + 1)
    // network params between input layer and hidden layer

    private val theta2: Matrix = randomMatrix(outputLayerSize, hiddenLayerSize + 1)
    // network params between hidden layer and output layer

    private val lambda = 0.0

    private val gradientSteps = 50

    init {
        println("Started training data initialization")
        val X = Matrix(trainingData.map { it.data }.toTypedArray())
        val y = trainingData.map { it.clazz }.toTypedArray()
        println("Finished training data initialization")
        val costFunction = { params: DoubleArray ->
            nnCostFunction(params, inputLayerSize, hiddenLayerSize, outputLayerSize, X, y, lambda)
        }
        println("Started optimization process")
        fmincg(costFunction, pack(theta1, theta2), gradientSteps)
    }

    fun predictResult(example: DoubleArray): Int {
        val a3 = predict(example, theta1, theta2)
        val result = DoubleArray(a3.rowCount)
        for (i in 0..a3.rowCount - 1) {
            result[i] = a3.getAt(i, 0)
        }
        return indMax(result)
    }

}
