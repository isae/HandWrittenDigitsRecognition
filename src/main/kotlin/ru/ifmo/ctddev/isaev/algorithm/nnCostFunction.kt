package ru.ifmo.ctddev.isaev.algorithm

/**
 * @author iisaev
 */

fun nnCostFunction(nn_params: DoubleArray,
                   input_layer_size: Int,
                   hidden_layer_size: Int,
                   num_labels: Int,
                   X: Matrix,
                   y: Array<Int>,
                   lambda: Double): CostGradientTuple {

    val Theta1 = reshape(nn_params.subArray(
            0, hidden_layer_size * (input_layer_size + 1)),
            hidden_layer_size, (input_layer_size + 1)
    )

    val Theta2 = reshape(nn_params.subArray(
            (hidden_layer_size * (input_layer_size + 1)), nn_params.size),
            num_labels, (hidden_layer_size + 1)
    )

    // Setup some useful variables
    val m = X.size

    // You need to return the following variables correctly
    var Theta1_grad = zeros(size(Theta1));
    var Theta2_grad = zeros(size(Theta2));

    // ====================== YOUR CODE HERE ======================
    // Instructions: You should complete the code by working through the
    //               following parts.
    //
    // Part 1: Feedforward the neural network and return the cost in the
    //         variable J. After implementing Part 1, you can verify that your
    //         cost function computation is correct by verifying the cost
    //         computed in ex4.m
    //
    // Part 2: Implement the backpropagation algorithm to compute the gradients
    //         Theta1_grad and Theta2_grad. You should return the partial derivatives of
    //         the cost function with respect to Theta1 and Theta2 in Theta1_grad and
    //         Theta2_grad, respectively. After implementing Part 2, you can check
    //         that your implementation is correct by running checkNNGradients
    //
    //         Note: The vector y passed into the function is a vector of labels
    //               containing values from 1..K. You need to map this vector into a
    //               binary vector of 1's and 0's to be used with the neural network
    //               cost function.
    //
    //         Hint: We recommend implementing backpropagation using a for-loop
    //               over the training examples if you are implementing it for the
    //               first time.
    //
    // Part 3: Implement regularization with the cost function and gradients.
    //
    //         Hint: You can implement this around the code for
    //               backpropagation. That is, you can compute the gradients for
    //               the regularization separately and then add them to Theta1_grad
    //               and Theta2_grad from Part 2.
    //


    //// Part 1 implementation

    val J = calculateCost(X, Theta1, Theta2, m, num_labels, y, lambda)

    //// Part 2 implementation


    for (t in 0..m - 1) {
        // For the input layer, where l=1:
        val a1 = fromColumn(X[t]).prependWithRowOf(1.0)

        // For the hidden layers, where l=2:
        val z2 = Theta1 * a1
        val a2 = sigmoid(z2)
                .prependWithRowOf(1.0)

        val z3 = Theta2 * a2
        val predictedValue = sigmoid(z3)

        val actualValue = fromColumn(
                IntRange(0, num_labels - 1)
                        .map { it == y[t] }
                        .map { if (it) 1.0 else 0.0 }
                        .toDoubleArray()
        )
        // For the delta values:
        val delta_3 = predictedValue - actualValue

        val delta_2 = (Theta2.t() * delta_3)
                .pointMul(sigmoidGradient(z2.copy()).prependWithRowOf(1.0))
                .trimFirstRow()

        // delta_1 is not calculated because we do not associate error with the input

        // Big delta update
        Theta1_grad += delta_2 * a1.t()
        Theta2_grad += delta_3 * a2.t()
    }

    Theta1_grad = (1 / m) * Theta1_grad + (lambda / m) * Theta1.trimFirstRow().prependWithRowOf(0.0)
    Theta2_grad = (1 / m) * Theta2_grad + (lambda / m) * Theta2.trimFirstRow().prependWithRowOf(0.0)


    // Unroll gradients
    val grad = pack(Theta1_grad, Theta2_grad)
    return CostGradientTuple(J, grad)

}

private fun calculateCost(X: Matrix, Theta1: Matrix, Theta2: Matrix, m: Int, num_labels: Int, y: Array<Int>, lambda: Double): Double {
    val a1 = X.prependWithColumnOf(1.0)

    val z2 = a1 * Theta1.t()
    var a2 = sigmoid(z2)
    a2 = a2.prependWithColumnOf(1.0)

    val z3 = a2 * Theta2.t()
    val a3 = sigmoid(z3)
    val hThetaX = a3

    val yVec = zeros(Pair(m, num_labels))

    for (i in 0..m - 1) {
        yVec.getData()[i][y[i]] = 1.0
    }

    val J = 1 / m * sum(-yVec.pointMul(log(hThetaX)) - (1 - yVec.copy()).pointMul(log(1 - hThetaX.copy())))

    val regularParam = (
            sum(pointPow(Theta1.trimFirstRow(), 2)) + sum(pointPow(Theta2.trimFirstRow(), 2))
            ) * (lambda / (2 * m))

    return J + regularParam
}

fun predict(obj: DoubleArray, Theta1: Matrix, Theta2: Matrix): Matrix {
    // For the input layer, where l=1:
    val a1 = fromColumn(obj)
            .prependWithRowOf(1.0)

    // For the hidden layers, where l=2:
    val z2 = Theta1 * a1
    val a2 = sigmoid(z2)
            .prependWithRowOf(1.0)

    val z3 = Theta2 * a2
    val a3 = sigmoid(z3)
    return a3
}

fun pack(m1: Matrix, m2: Matrix): DoubleArray {
    val result = DoubleArray(m1.rowCount * m1.columnCount + m2.rowCount * m2.columnCount)
    for (i in 0..m1.columnCount - 1)
        for (j in 0..m1.rowCount - 1) {
            result[m1.rowCount * i + j] = m1.getAt(j, i)
        }
    val pos = m1.rowCount * m1.columnCount
    for (i in 0..m2.columnCount - 1)
        for (j in 0..m2.rowCount - 1) {
            result[pos + m2.rowCount * i + j] = m2.getAt(j, i)
        }
    return result
}

private operator fun Double.times(matrix: Matrix): Matrix {
    return matrix.apply { this * it }
}

fun sigmoidGradient(z: Matrix): Matrix {
    return z.apply { sigmoidValue(it) * (1 - sigmoidValue(it)) }
}

fun pointPow(trimFirstRow: Matrix, i: Int): Matrix {
    return trimFirstRow.apply { Math.pow(it, i.toDouble()) }
}

private operator fun Int.times(pointMul: Matrix): Matrix {
    return pointMul.apply { this * it }
}

fun sum(matrix: Matrix): Double {
    return matrix.sum()
}

fun log(matrix: Matrix): Matrix {
    return matrix.apply { Math.log(it) }
}

operator fun Int.minus(matrix: Matrix): Matrix {
    return matrix.apply { this - it }
}

fun zeros(size: Pair<Int, Int>): Matrix {
    return Matrix(size.first, size.second)
}

fun DoubleArray.subArray(from: Int, to: Int): DoubleArray {
    val result = DoubleArray(to - from)
    System.arraycopy(this, from, result, 0, to - from)
    return result
}

fun reshape(arr: DoubleArray, rowCount: Int, colCount: Int): Matrix {
    val result = Array(rowCount, { kotlin.DoubleArray(colCount) })
    for (i in 0..colCount - 1)
        for (j in 0..rowCount - 1) {
            result[j][i] = arr[i * rowCount + j]
        }
    return Matrix(result)
}

fun size(matr: Matrix): Pair<Int, Int> {
    return Pair(matr.rowCount, matr.columnCount)
}