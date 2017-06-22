package ru.ifmo.ctddev.isaev.algorithm

class NeuralNetwork(private val inputLayerSize: Int,
                    private val hiddenLayerSize: Int,
                    private val numLabels: Int,
                    private val trainingData: List<TrainObject>) {

    private val theta1: Matrix
    // network params between input layer and hidden layer

    private val theta2: Matrix
    // network params between hidden layer and output layer

    private val lambda = 0.0

    private val gradientSteps = 50

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

        theta1 = reshape(newX.subArray(
                0, hiddenLayerSize * (inputLayerSize + 1)),
                hiddenLayerSize, (inputLayerSize + 1)
        )

        theta2 = reshape(newX.subArray(
                (hiddenLayerSize * (inputLayerSize + 1)), newX.size),
                numLabels, (hiddenLayerSize + 1)
        )
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
