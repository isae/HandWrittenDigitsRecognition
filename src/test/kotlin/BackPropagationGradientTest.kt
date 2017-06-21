import org.junit.Test
import ru.ifmo.ctddev.isaev.algorithm.*
import kotlin.test.assertTrue

/**
 * @author iisaev
 */

class BackPropagationGradientTest {
    val inputLayerSize = 3
    val hiddenLayerSize = 5
    val numLabels = 3
    val m = 5
    val lambda = 0.0

    @Test
    fun testBackPropagationGradients() {
        // We generate some 'random' test data
        val Theta1 = debugInitializeWeights(hiddenLayerSize, inputLayerSize)
        val Theta2 = debugInitializeWeights(numLabels, hiddenLayerSize)
        // Reusing debugInitializeWeights to generate X
        val X = debugInitializeWeights(m, inputLayerSize - 1)
        val y = 1.rangeTo(m)
                .map { it % numLabels }
                .toTypedArray()

        // Unroll parameters
        val nn_params = pack(Theta1, Theta2)

        // Short hand for cost function
        val costFunc = { params: DoubleArray ->
            nnCostFunction(params, inputLayerSize, hiddenLayerSize, numLabels, X.copy(), y, lambda)
        }

        val costGrad = costFunc(nn_params)
        val grad = costGrad.gradient
        val numgrad = computeNumericalGradient(
                { t -> costFunc(t).cost }
                , nn_params)

        val diff = norm(numgrad - grad) / norm(numgrad + grad)

        assertTrue(Math.abs(diff) < 10e-9, "Diff is lesser than 1e-9")

    }

    private fun norm(doubles: DoubleArray): Double {
        val squares = doubles.map { Math.pow(it, 2.0) }
        return Math.sqrt(squares.sum())
    }

    private fun debugInitializeWeights(hiddenLayerSize: Int, inputLayerSize: Int): Matrix {
        val size = hiddenLayerSize * (inputLayerSize + 1)
        return reshape(
                1.rangeTo(size).map { Math.sin(it.toDouble()) / 10 }.toDoubleArray(),
                hiddenLayerSize, inputLayerSize + 1
        )
    }

    private fun computeNumericalGradient(J: (DoubleArray) -> Double, theta: DoubleArray): DoubleArray {

        val numgrad = DoubleArray(theta.size)
        val perturb = DoubleArray(theta.size)
        val e = 1e-4
        for (p in 0..theta.size - 1) {
            // Set perturbation vectorlll
            perturb[p] = e
            val loss1 = J(theta - perturb)
            val loss2 = J(theta + perturb)
            // Compute Numerical Gradientl
            numgrad[p] = (loss2 - loss1) / (2.0 * e)
            perturb[p] = 0.0
        }
        return numgrad
    }
}