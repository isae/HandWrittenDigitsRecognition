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
        val dataCopy = data.copyOf()
        for (i in 0..data.size - 1)
            for (j in 0..this[i].size - 1)
                dataCopy[i][j] = function(dataCopy[i][j])
        return Matrix(dataCopy)
    }

    operator fun times(other: Matrix): Matrix {
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
        val result = Matrix(rowCount, columnCount + 1)
        for (i in 0..rowCount - 1) {
            result[i][0] = d
            System.arraycopy(this[i], 0, result[i], 1, columnCount)
        }
        return result
    }

    override fun toString(): String {
        return "[${rowCount}x$columnCount]"
    }

    operator fun unaryMinus(): Matrix {
        return 0 - this
    }

    fun zipWith(m1: Matrix, m2: Matrix, op: (Double, Double) -> Double): Matrix {
        if (m1.rowCount != m2.rowCount || m1.columnCount != m2.columnCount) {
            throw IllegalArgumentException("Cannot perform point operation: " +
                    "$m1 does not match with $m2")
        }
        return Matrix(
                m1.data.zip(m2.data).map {
                    it.first.zip(it.second)
                            .map { op(it.first, it.second) }
                            .toDoubleArray()
                }.toTypedArray()
        )
    }

    operator fun plus(other: Matrix): Matrix {
        return zipWith(this, other, Double::plus)
    }

    fun trimFirstRow(): Matrix {
        return Matrix(
                this.data.toList()
                        .subList(1, this.data.size)
                        .toTypedArray()
        )
    }

    operator fun minus(other: Matrix): Matrix {
        return zipWith(this, other, Double::minus)
    }

    fun pointMul(other: Matrix): Matrix {
        return zipWith(this, other, Double::times)
    }

    fun sum(): Double {
        return data.map { it.sum() }.sum()
    }

    fun prependWithRowOf(value: Double): Matrix {
        val result = Matrix(rowCount + 1, columnCount)
        val ones = DoubleArray(columnCount)
        java.util.Arrays.fill(ones, value)
        result.data[0] = ones
        for (i in 1..rowCount) {
            result.data[i] = data[i - 1]
        }
        return result
    }

    fun getData(): Array<DoubleArray> {
        return data
    }

    fun getAt(row: Int, col: Int): Double {
        return data[row][col]
    }

}