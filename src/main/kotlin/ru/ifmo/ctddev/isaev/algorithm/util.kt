package ru.ifmo.ctddev.isaev.algorithm

import kotlin.streams.toList

/**
 * @author iisaev
 */

data class CostGradientTuple(val cost: Double, val gradient: DoubleArray)

data class TrainObject(val data: DoubleArray, val clazz: Int)

private var counter = 0

fun readDataSet(): List<TrainObject> {
    println("Started reading of dataset")
    try {
        return java.io.BufferedReader(java.io.FileReader("./resources/train.csv")).use {
            it.lines()
                    .skip(1)
                    .limit(5)
                    .map { it.split(',') }
                    .map { it.map { it.toDouble() } }
                    .map {
                        println("Read row ${++counter}")
                        TrainObject(it.subList(1, it.size).toDoubleArray(), it[0].toInt())
                    }
                    .toList()

        }
    } finally {
        println("Finished reading of dataset")
    }
}

fun sigmoid(arg: Matrix): Matrix {
    return arg.apply(::sigmoidValue)
}

fun sigmoidValue(arg: Double): Double {
    return 1 / (1 + Math.exp((-arg)))
}

fun indMax(arr: DoubleArray): Int {
    var max = java.lang.Double.MIN_VALUE
    var result = -1
    for (i in arr.indices) {
        if (arr[i] > max) {
            max = arr[i]
            result = i
        }
    }
    return result
}

fun fromColumn(column: DoubleArray): Matrix {
    val result = Array(column.size) { DoubleArray(1) }
    for (i in column.indices) {
        result[i][0] = column[i]
    }
    return Matrix(result)
}

fun randomMatrix(rowCount: Int, columnCount: Int): Matrix {
    val result = Matrix(rowCount, columnCount)
    for (i in 0..rowCount - 1) {
        for (j in 0..columnCount - 1) {
            result[i][j] = RANDOM.nextDouble() * 2.0 * EPSILON_INIT - EPSILON_INIT
        }
    }
    return result
}