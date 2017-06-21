package ru.ifmo.ctddev.isaev.algorithm

/**
 * @author iisaev
 */
val RANDOM = java.util.Random()

val EPSILON_INIT = 0.12

class Matrix {

    private val data: Array<DoubleArray>

    val rowCount: Int

    val columnCount: Int

    val size: Int

    private var invalid = false

    constructor(data: Array<DoubleArray>) {
        this.data = data
        this.size = data.size
        this.rowCount = data.size
        if (rowCount == 0) {
            throw IllegalStateException("Matrix cannot be zero-height")
        }
        this.columnCount = data[0].size
        if (columnCount == 0) {
            throw IllegalStateException("Matrix cannot be zero-width")
        }
        if (!data.toList().any({ arr -> arr.size == columnCount })) {
            throw IllegalStateException("Invalid matrix column")
        }
    }

    constructor(rowCount: Int, columnCount: Int) {
        this.rowCount = rowCount
        this.columnCount = columnCount
        this.data = Array(rowCount) { DoubleArray(columnCount) }
        this.size = data.size
    }

    fun apply(function: (Double) -> Double): Matrix {
        Lock().use {
            for (i in 0..data.size - 1)
                for (j in 0..this[i].size - 1)
                    data[i][j] = function(data[i][j])

            return Matrix(data)
        }
    }

    operator fun times(other: Matrix): Matrix {
        assertValid()
        if (other.rowCount != columnCount) {
            throw IllegalArgumentException("Cannot multiply: [${rowCount}x${columnCount}] by [${other.rowCount}x${other.columnCount}]")
        }
        val result = Matrix(rowCount, other.columnCount)
        for (i in 0..rowCount - 1) {
            for (j in 0..other.columnCount - 1) {
                val sum = (0..columnCount - 1)
                        .sumByDouble { data[i][it] * other.data[it][j] }
                result[i][j] = sum
            }
        }
        return result
    }

    fun t(): Matrix {
        assertValid()
        val result = Matrix(columnCount, rowCount)
        for (i in 0..rowCount - 1) {
            for (j in 0..columnCount - 1) {
                result[j][i] = this[i][j]
            }
        }
        return result
    }

    operator fun get(t: Int): DoubleArray {
        return data[t]
    }

    fun prependWithColumnOf(d: Double): Matrix {
        Lock().use {
            val result = Matrix(rowCount, columnCount + 1)
            for (i in 0..rowCount - 1) {
                result[i][0] = d
                System.arraycopy(this[i], 0, result[i], 1, columnCount)
            }
            return result
        }
    }

    override fun toString(): String {
        return "[${rowCount}x$columnCount]"
    }

    operator fun unaryMinus(): Matrix {
        return 0 - this
    }

    fun zipWith(m2: Matrix, op: (Double, Double) -> Double): Matrix {
        if (rowCount != m2.rowCount || columnCount != m2.columnCount) {
            throw IllegalArgumentException("Cannot perform point operation: " +
                    "$this does not match with $m2")
        }
        Lock().use {
            for (i in 0..rowCount - 1)
                for (j in 0..columnCount - 1) {
                    data[i][j] = op(data[i][j], m2.data[i][j])
                }
            return Matrix(data)
        }
    }

    operator fun plus(other: Matrix): Matrix {
        return zipWith(other, Double::plus)
    }

    operator fun minus(other: Matrix): Matrix {
        return zipWith(other, Double::minus)
    }

    fun pointMul(other: Matrix): Matrix {
        return zipWith(other, Double::times)
    }

    fun trimFirstRow(): Matrix {
        Lock().use {
            return Matrix(
                    Array(rowCount - 1,
                            { i -> data[i + 1] }
                    )
            )
        }
    }

    fun sum(): Double {
        return data.map { it.sum() }.sum()
    }

    fun prependWithRowOf(value: Double): Matrix {
        Lock().use {
            val ones = DoubleArray(columnCount)
            java.util.Arrays.fill(ones, value)
            return Matrix(Array(rowCount + 1,
                    { i ->
                        if (i == 0) ones else data[i - 1]
                    }
            ))
        }
    }

    fun getData(): Array<DoubleArray> {
        return data
    }

    fun getAt(row: Int, col: Int): Double {
        return data[row][col]
    }

    inner class Lock : AutoCloseable {
        override fun close() {
            invalid = true
        }

        init {
            assertValid()
        }
    }

    fun copy(): Matrix {
        assertValid()
        val result = Array(rowCount, { i -> data[i].copyOf() })
        return Matrix(result)
    }

    private fun assertValid() {
        if (invalid) {
            throw IllegalStateException("Requested operation on invalid data!")
        }
    }

    fun zeroFirstColumn(): Matrix {
        Lock().use {
            for (i in 0..rowCount - 1) {
                data[i][0] = 0.0
            }
            return Matrix(data)
        }
    }
}