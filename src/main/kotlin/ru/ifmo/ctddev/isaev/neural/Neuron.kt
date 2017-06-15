package ru.ifmo.ctddev.isaev.neural

import ru.ifmo.ctddev.isaev.sigmoidValue
import java.util.*

class Neuron {

    private var inputs: ArrayList<Int>? = null
    private val weights: ArrayList<Double>
    private var biasWeight: Double = 0.toDouble()
    val output: Double  by lazy { calculateOutput() }

    init {
        this.inputs = ArrayList<Int>()
        this.weights = ArrayList<Double>()
        this.biasWeight = Math.random()
    }

    fun setInputs(inputs: ArrayList<Int>) {
        if (this.inputs!!.size == 0) {
            this.inputs = ArrayList(inputs)
            generateWeights()
        }

        this.inputs = ArrayList(inputs)
    }

    private fun generateWeights() {
        for (i in inputs!!.indices) {
            weights.add(Math.random())
        }
    }

    fun calculateOutput(): Double {
        var sum = inputs!!.indices.sumByDouble { inputs!![it] * weights[it] }

        sum += BIAS * biasWeight

        return sigmoidValue(sum)
    }

    fun adjustWeights(delta: Double) {
        for (i in inputs!!.indices) {
            var d = weights[i]
            d += LEARNING_RATIO * delta * inputs!![i].toDouble()
            weights[i] = d
        }

        biasWeight += LEARNING_RATIO * delta * BIAS.toDouble()
    }

    companion object {

        private val BIAS = 1
        private val LEARNING_RATIO = 0.1
    }

}
