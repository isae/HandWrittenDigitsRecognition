package ru.ifmo.ctddev.isaev

import ru.ifmo.ctddev.isaev.data.Matrix
import java.io.BufferedReader
import java.io.FileReader
import kotlin.streams.toList

/**
 * @author iisaev
 */

data class CostGradientTuple(val cost: Double, val gradient: DoubleArray)

data class TrainObject(val data: DoubleArray, val clazz: Int)

fun readDataSet(): List<TrainObject> {
    return BufferedReader(FileReader("./resources/train.csv")).use {
        it.lines().skip(1)
                .map { it.split(',') }
                .map { it.map { it.toDouble() } }
                .map { TrainObject(it.subList(1, it.size).toDoubleArray(), it[0].toInt()) }
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