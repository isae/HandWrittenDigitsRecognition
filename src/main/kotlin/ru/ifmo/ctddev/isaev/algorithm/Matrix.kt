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

    constructor(data: Array<DoubleArray>) {
        this.data = data
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
    }

    fun apply(function: (Double) -> Double): Matrix {
        val dataCopy = data.copyOf()
        for (i in 0..data.size)
            for (j in 0..this[i].size)
                dataCopy[i][j] = function(dataCopy[i][j])
        return Matrix(dataCopy)
    }

    fun fillRandom(): Matrix {
        val result = Matrix(data)
        for (i in 0..rowCount) {
            for (j in 0..columnCount) {
                result[i][j] = RANDOM.nextDouble() * 2.0 * EPSILON_INIT - EPSILON_INIT
            }
        }
        return result
    }

    operator fun times(other: Matrix): Matrix {
        if (other.rowCount != columnCount) {
            throw IllegalStateException("Invalid matrices to multiply")
        }
        val result = Matrix(rowCount, other.columnCount)
        for (i in 0..rowCount) {
            for (j in 0..other.columnCount) {
                val sum = (0..columnCount)
                        .sumByDouble { data[i][it] * other.data[it][j] }
                result[i][j] = sum
            }
        }
        return result
    }

    fun t(): Matrix {
        val result = Matrix(columnCount, rowCount)
        for (i in 0..rowCount) {
            for (j in 0..columnCount) {
                result[j][i] = this[i][j]
            }
        }
        return result
    }

    operator fun get(t: Int): DoubleArray {
        return data[t]
    }
}