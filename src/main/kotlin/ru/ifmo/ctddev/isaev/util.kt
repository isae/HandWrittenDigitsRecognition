package ru.ifmo.ctddev.isaev

import java.io.BufferedReader
import java.io.FileReader
import kotlin.streams.toList

data class TrainObject(val data: IntArray, val clazz: Int)

fun readDataSet(): List<TrainObject> {
    return BufferedReader(FileReader("./resources/train.csv")).use {
        it.lines().skip(1)
                .map { it.split(',') }
                .map { it.map { it.toInt() } }
                .map { TrainObject(it.subList(1, it.size).toIntArray(), it[0]) }
                .toList()
    }
}

fun sigmoidValue(arg: Double): Double {
    return 1 / (1 + Math.exp((-arg)))
}
