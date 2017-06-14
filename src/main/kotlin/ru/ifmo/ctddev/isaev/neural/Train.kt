package ru.ifmo.ctddev.isaev.neural

import ru.ifmo.ctddev.isaev.data.ReadWriteFile

import java.util.ArrayList

class Train {

    private val network: Network
    private val trainingSets: ArrayList<TrainingSet>

    init {
        this.network = Network()
        this.network.addNeurons(NEURON_COUNT)
        this.trainingSets = ReadWriteFile.readTrainingSets()
    }

    fun train(count: Long) {
        for (i in 0..count - 1) {
            val index = (Math.random() * trainingSets.size).toInt()
            val set = trainingSets[index]
            network.setInputs(set.inputs)
            network.adjustWages(set.goodOutput)
        }
    }

    fun setInputs(inputs: ArrayList<Int>) {
        network.setInputs(inputs)
    }

    fun addTrainingSet(newSet: TrainingSet) {
        trainingSets.add(newSet)
    }

    val outputs: ArrayList<Double>
        get() = network.outputs

    companion object {

        private val NEURON_COUNT = 26
    }

}
