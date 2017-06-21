import org.junit.Test
import ru.ifmo.ctddev.isaev.algorithm.Matrix
import ru.ifmo.ctddev.isaev.algorithm.pack
import ru.ifmo.ctddev.isaev.algorithm.reshape
import ru.ifmo.ctddev.isaev.algorithm.subArray
import java.util.*

/**
 * @author iisaev
 */

class UtilTest {
    val random = Random()
    @Test
    fun testPackUnpack() {
        val testM1 = Matrix(
                arrayOf(
                        DoubleArray(5, { random.nextDouble() }),
                        DoubleArray(5, { random.nextDouble() }),
                        DoubleArray(5, { random.nextDouble() })
                )
        )

        val testM2 = Matrix(
                arrayOf(
                        DoubleArray(4, { random.nextDouble() }),
                        DoubleArray(4, { random.nextDouble() })
                )
        )
        val packed = pack(testM1, testM2)

        val Theta1 = reshape(packed.subArray(0, 15), 3, 5)

        val Theta2 = reshape(packed.subArray(15, packed.size), 2, 4)

        assertMatrixEquals(testM1, Theta1, 1e-4)
        assertMatrixEquals(testM2, Theta2, 1e-4)

    }

    @Test
    fun testCopy() {
        val testM1 = Matrix(
                arrayOf(
                        DoubleArray(5, { random.nextDouble() }),
                        DoubleArray(5, { random.nextDouble() }),
                        DoubleArray(5, { random.nextDouble() })
                )
        )
        assertMatrixEquals(testM1, testM1.copy(), 1e-8)
    }

    @Test
    fun testMatrixMultiplication() {
        val m1 = Matrix(
                arrayOf(
                        arrayOf(7, 3, 4).map { it.toDouble() }.toDoubleArray()
                )
        )
        val m2 = Matrix(
                arrayOf(
                        arrayOf(1, 5).map { it.toDouble() }.toDoubleArray(),
                        arrayOf(2, 9).map { it.toDouble() }.toDoubleArray(),
                        arrayOf(6, 3).map { it.toDouble() }.toDoubleArray()
                )
        )

        val mulResult = m1 * m2
        val expectedMulResult = Matrix(
                arrayOf(
                        arrayOf(37, 74).map { it.toDouble() }.toDoubleArray()
                )
        )
        assertMatrixEquals(expectedMulResult, mulResult, 1e-6)
    }
}