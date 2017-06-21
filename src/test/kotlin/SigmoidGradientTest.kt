
import org.junit.Test
import ru.ifmo.ctddev.isaev.algorithm.Matrix
import ru.ifmo.ctddev.isaev.algorithm.sigmoidGradient

/**
 * @author iisaev
 */

class SigmoidGradientTest {
    @Test
    fun testSigmoidGradient() {
        val testMatrix = Matrix(
                arrayOf(
                        arrayOf(1.0, 0.5, 0.0, 0.5, 1.0).toDoubleArray()
                )
        )
        val gradient = sigmoidGradient(testMatrix)
        val expectedResult = Matrix(
                arrayOf(
                        arrayOf(0.196612, 0.235004, 0.250000, 0.235004, 0.196612)
                                .toDoubleArray()
                )
        )

        assertMatrixEquals(expectedResult, gradient, 1e-4)

    }
}