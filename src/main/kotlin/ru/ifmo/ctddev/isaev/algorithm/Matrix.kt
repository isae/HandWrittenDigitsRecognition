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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun fillRandom(): Matrix {
        val result = Matrix(data)
        for (i in 0..rowCount) {
            for (j in 0..columnCount) {
                result.data[i][j] = RANDOM.nextDouble() * 2.0 * EPSILON_INIT - EPSILON_INIT
            }
        }
        return result
    }

    companion object {

        fun fromColumn(column: DoubleArray): Matrix {
            val result = Matrix(column.size, 1)
            for (i in column.indices) {
                result.data[i][0] = column[i]
            }
            return result
        }

        fun fromRow(column: DoubleArray): Matrix {
            return Matrix(arrayOf(column))
        }
    }

    operator fun times(a1: Any): Matrix {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun t(): Matrix {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    operator fun get(t: Int): DoubleArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}