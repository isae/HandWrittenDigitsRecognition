import ru.ifmo.ctddev.isaev.algorithm.Matrix
import kotlin.test.AssertionError

/**
 * @author iisaev
 */

fun assertMatrixEquals(expected: Matrix, actual: Matrix, epsilon: Double) {
    if (expected.rowCount != actual.rowCount || expected.columnCount != actual.columnCount) {
        throw AssertionError("""
        Expected size: [${expected.rowCount}x${expected.columnCount}]
        Actual size: [${actual.rowCount}x${actual.columnCount}]
""")
    }
    for (i in 0..expected.rowCount - 1)
        for (j in 0..expected.columnCount - 1) {
            val expectedValue = expected.getAt(i, j)
            val actualValue = actual.getAt(i, j)
            if (Math.abs(expectedValue - actualValue) > epsilon) {
                throw AssertionError("Error at position (${i}x$j): expected $expectedValue, but found $actualValue")
            }
        }
}