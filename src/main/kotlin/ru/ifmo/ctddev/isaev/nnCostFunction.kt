package ru.ifmo.ctddev.isaev

import ru.ifmo.ctddev.isaev.data.Matrix

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
    val Theta1_grad = zeros(size(Theta1));
    val Theta2_grad = zeros(size(Theta2));

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

    var z2 = a1 * Theta1.t()
    var a2 = sigmoid(z2)
    a2 = a2.prependWithRowOfOne()

    var z3 = a2 * Theta2.t()
    var a3 = sigmoid(z3)
    val hThetaX = a3

    val yVec = zeros(Pair(m, num_labels))

    for (i in 1..m) {
        yVec.data[i][y[i]] = 1.0
    }

    J = 1 / m * sum(-1 * pointMul(yVec, log(hThetaX)) - pointMul((1 - yVec), log(1 - hThetaX)))

    val regularParam = (
            sum(pointPow(trimFirstRow(Theta1), 2)) + sum(pointPow(trimFirstRow(Theta2), 2))
            ) * (lambda / (2 * m))

    J += regularParam

    //// Part 2 implementation


    for (t in 0..m) {
        // For the input layer, where l=1:
        val a1 = [1; X(t, :).t()];

        // For the hidden layers, where l=2:
        z2 = Theta1 * a1;
        val a2 = [1; sigmoid(z2)];

        z3 = Theta2 * a2
        a3 = sigmoid(z3)

        val yy = ([1:num_labels] == y(t)).t();
        // For the delta values:
        val delta_3 = a3 - yy;

        val delta_2 = (Theta2.t() * delta_3).* [1; sigmoidGradient(z2)]
        val delta_2 = delta_2(2:end) // Taking of the bias row

        // delta_1 is not calculated because we do not associate error with the input

        // Big delta update
        Theta1_grad = Theta1_grad + delta_2 * a1.t()
        Theta2_grad = Theta2_grad + delta_3 * a2.t()
    }

    val Theta1_grad = (1 / m) * Theta1_grad + (lambda / m) * [zeros(size(Theta1, 1), 1) Theta1 (:, 2:end)]
    val Theta2_grad = (1 / m) * Theta2_grad + (lambda / m) * [zeros(size(Theta2, 1), 1) Theta2 (:, 2:end)]


    // -------------------------------------------------------------

    // =========================================================================

    // Unroll gradients
    val grad = [Theta1_grad(:) ; Theta2_grad(:)];
    return CostGradientTuple(J, grad)

}

fun pointPow(trimFirstRow: Matrix, i: Int): Matrix {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private operator fun Matrix.minus(pointMul: Matrix): Matrix {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private operator fun Int.times(pointMul: Matrix): Matrix {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun sum(any: Matrix): Double {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun pointMul(matrix: Matrix, log: Matrix): Matrix {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun log(hThetaX: Matrix): Matrix {}

private operator fun Int.minus(matrix: Matrix): Matrix {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

}

private fun size(matr: Matrix): Pair<Int, Int> {
    return Pair(matr.rowCount, matr.columnCount)
}

fun trimFirstRow(matr: Matrix): Matrix {
    return Theta1(:, 2:end)
}