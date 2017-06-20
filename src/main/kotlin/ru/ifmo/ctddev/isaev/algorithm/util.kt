package ru.ifmo.ctddev.isaev.algorithm

import kotlin.streams.toList

/**
 * @author iisaev
 */

data class CostGradientTuple(val cost: Double, val gradient: DoubleArray)

data class TrainObject(val data: DoubleArray, val clazz: Int)

fun readDataSet(): List<ru.ifmo.ctddev.isaev.algorithm.TrainObject> {
    return java.io.BufferedReader(java.io.FileReader("./resources/train.csv")).use {
        it.lines().skip(1)
                .map { it.split(',') }
                .map { it.map { it.toDouble() } }
                .map { ru.ifmo.ctddev.isaev.algorithm.TrainObject(it.subList(1, it.size).toDoubleArray(), it[0].toInt()) }
                .toList()
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
    val result = Matrix(column.size, 1)
    for (i in column.indices) {
        result.data[i][0] = column[i]
    }
    return result
}


fun randomMatrix(rowCount: Int, columnCount: Int): Matrix {
    val result = Matrix(rowCount, columnCount)
    for (i in 0..rowCount-1) {
        for (j in 0..columnCount-1) {
            result[i][j] = RANDOM.nextDouble() * 2.0 * EPSILON_INIT - EPSILON_INIT
        }
    }
    return result
}