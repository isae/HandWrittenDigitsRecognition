package ru.ifmo.ctddev.isaev.utils

object MathUtils {

    fun sigmoidValue(arg: Double): Double {
        return 1 / (1 + Math.exp((-arg)))
    }

}
