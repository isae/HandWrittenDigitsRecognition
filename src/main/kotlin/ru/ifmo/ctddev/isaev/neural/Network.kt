package ru.ifmo.ctddev.isaev.neural

import java.util.ArrayList

class Network {

    private val neurons: ArrayList<Neuron> = ArrayList()

    fun addNeurons(count: Int) {
        for (i in 0..count - 1)
            neurons.add(Neuron())
    }

    fun setInputs(inputs: ArrayList<Int>) {
        for (n in neurons)
            n.setInputs(inputs)
    }

    val outputs: ArrayList<Double>
        get() {
            val outputs = ArrayList<Double>()
            for (n in neurons)
                outputs.add(n.output)

            return outputs
        }

    fun adjustWages(goodOutput: ArrayList<Double>) {
        for (i in neurons.indices) {
            val delta = goodOutput[i] - neurons[i].output
            neurons[i].adjustWeights(delta)
        }
    }

}
