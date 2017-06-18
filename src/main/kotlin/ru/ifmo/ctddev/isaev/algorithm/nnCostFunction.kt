package ru.ifmo.ctddev.isaev.algorithm

import java.util.*

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
            (1 + (hidden_layer_size * (input_layer_size + 1))), nn_params.size),
            num_labels, (hidden_layer_size + 1)
    )

    // Setup some useful variables
    val m = X[0].size

    // You need to return the following variables correctly
    var J = 0.0
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

    val a1 = X.prependWithRowOfOne()

    val z2 = a1 * Theta1.t()
    var a2 = sigmoid(z2)
    a2 = a2.prependWithRowOfOne()

    val z3 = a2 * Theta2.t()
    var a3 = sigmoid(z3)
    val hThetaX = a3

    val yVec = zeros(Pair(m, num_labels))

    for (i in 1..m) {
        yVec.data[i][y[i]] = 1.0
    }

    J = 1 / m * sum(-1 * yVec.pointMul(log(hThetaX)) - (1 - yVec).pointMul(log(1 - hThetaX)))

    val regularParam = (
            sum(pointPow(Theta1.trimFirstRow(), 2)) + sum(pointPow(Theta2.trimFirstRow(), 2))
            ) * (lambda / (2 * m))

    J += regularParam

    //// Part 2 implementation


    for (t in 0..m) {
        a3 = predict(X[t], Theta1, Theta2)

        val yy = fromColumn(
                IntRange(0, num_labels - 1)
                        .map { it == y[t] }
                        .map { if (it) 1.0 else 0.0 }
                        .toDoubleArray()
        )
        // For the delta values:
        val delta_3 = a3 - yy

        val delta_2 = (Theta2.t() * delta_3)
                .pointMul(sigmoidGradient(z2).prependWithRowOfOne())
                .trimFirstRow()

        // delta_1 is not calculated because we do not associate error with the input

        // Big delta update
        Theta1_grad += delta_2 * a1.t()
        Theta2_grad += delta_3 * a2.t()
    }

    Theta1_grad = (1 / m) * Theta1_grad + (lambda / m) * Theta1.trimFirstRow().prependWithRowOfZeros()
    Theta2_grad = (1 / m) * Theta2_grad + (lambda / m) * Theta2.trimFirstRow().prependWithRowOfZeros()


    // Unroll gradients
    val grad = pack(Theta1_grad, Theta2_grad);
    return CostGradientTuple(J, grad)

}

fun predict(obj: DoubleArray, Theta1: Matrix, Theta2: Matrix): Matrix {
    // For the input layer, where l=1:
    val a1 = fromColumn(obj).prependWithRowOfOne().t()

    // For the hidden layers, where l=2:
    val z2 = Theta1 * a1;
    val a2 = sigmoid(z2).prependWithRowOfOne()

    val z3 = Theta2 * a2
    val a3 = sigmoid(z3)
    return a3
}

fun pack(m1: Matrix, m2: Matrix): DoubleArray {
    val result = DoubleArray(m1.rowCount * m1.columnCount + m2.rowCount + m2.columnCount)
    var pos = 0
    m1.data.forEach {
        System.arraycopy(it, 0, result, pos, m1.columnCount)
        pos += m1.columnCount
    }
    m2.data.forEach {
        System.arraycopy(it, 0, result, pos, m2.columnCount)
        pos += m2.columnCount
    }
    return result
}

private operator fun Double.times(matrix: Matrix): Matrix {
    return matrix.apply { this * it }
}

private fun prependWithRowOf(m: Matrix, value: Double): Matrix {
    val result = Matrix(m.rowCount + 1, m.columnCount)
    val ones = DoubleArray(m.columnCount)
    java.util.Arrays.fill(ones, value)
    result.data[0] = ones
    for (i in 1..m.rowCount + 1) {
        result.data[i] = m.data[i - 1]
    }
    return result
}

private fun Matrix.prependWithRowOfZeros(): Matrix {
    return prependWithRowOf(this, 0.0)
}

fun Matrix.prependWithRowOfOne(): Matrix {
    return prependWithRowOf(this, 1.0)
}

private fun zipWith(m1: Matrix, m2: Matrix, op: (Double, Double) -> Double): Matrix {
    if (m1.rowCount != m2.rowCount || m1.columnCount != m2.columnCount) {
        throw IllegalArgumentException("Cannot sum matrices")
    }
    return Matrix(
            m1.data.zip(m2.data).map {
                it.first.zip(it.second)
                        .map { op(it.first, it.second) }
                        .toDoubleArray()
            }.toTypedArray()
    )
}

private operator fun Matrix.plus(other: Matrix): Matrix {
    return zipWith(this, other, Double::plus)
}

private fun Matrix.trimFirstRow(): Matrix {
    return Matrix(
            this.data.toList()
                    .subList(1, this.data.size)
                    .toTypedArray()
    )
}

fun sigmoidGradient(z: Matrix): Matrix {
    return sigmoid(z).pointMul((1 - sigmoid(z)))
}

fun pointPow(trimFirstRow: Matrix, i: Int): Matrix {
    return trimFirstRow.apply { Math.pow(it, i.toDouble()) }
}

private operator fun Matrix.minus(other: Matrix): Matrix {
    return zipWith(this, other, Double::minus)
}

private operator fun Int.times(pointMul: Matrix): Matrix {
    return pointMul.apply { this * it }
}

fun sum(matrix: Matrix): Double {
    return matrix.data.map { it.sum() }.sum()
}

fun Matrix.pointMul(other: Matrix): Matrix {
    return zipWith(this, other, Double::times)
}

fun log(matrix: Matrix): Matrix {
    return matrix.apply { Math.log(it) }
}

private operator fun Int.minus(matrix: Matrix): Matrix {
    return matrix.apply { this - it }
}

fun zeros(size: Pair<Int, Int>): Matrix {
    return Matrix(size.first, size.second)
}

private fun DoubleArray.subArray(from: Int, to: Int): DoubleArray {
    val result = DoubleArray(to - from)
    System.arraycopy(this, from, result, 0, to - from)
    return result
}

private fun reshape(arr: DoubleArray, rowCount: Int, colCount: Int): Matrix {
    return Matrix(
            (0..rowCount)
                    .mapTo(ArrayList<DoubleArray>()) {
                        arr.subArray(it * colCount, (it + 1) * colCount)
                    }
                    .toTypedArray()
    )
}

private fun size(matr: Matrix): Pair<Int, Int> {
    return Pair(matr.rowCount, matr.columnCount)
}