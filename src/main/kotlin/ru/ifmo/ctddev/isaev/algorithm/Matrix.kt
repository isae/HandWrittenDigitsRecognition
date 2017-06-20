package ru.ifmo.ctddev.isaev.algorithm

/**
 * @author iisaev
 */
val RANDOM = java.util.Random()

val EPSILON_INIT = 0.12

class Matrix {

    val data: Array<DoubleArray>

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

}