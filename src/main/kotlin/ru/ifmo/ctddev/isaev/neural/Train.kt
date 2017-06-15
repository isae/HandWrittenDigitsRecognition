package ru.ifmo.ctddev.isaev.neural

import ru.ifmo.ctddev.isaev.TrainObject
import ru.ifmo.ctddev.isaev.readDataSet
import java.util.*

val NEURON_COUNT = 26

class Train(val network: Network = Network(),
            val trainData: List<TrainObject> = readDataSet()) {

    init {
        this.network.addNeurons(NEURON_COUNT)
    }

    fun train(count: Long) {
        for (i in 0..count - 1) {
            val index = (Math.random() * trainData.size).toInt()
            val set = trainData[index]
        }
    }

    fun setInputs(inputs: ArrayList<Int>) {
        network.setInputs(inputs)
    }

    /*fun addTrainingSet(newSet: TrainingSet) {
        trainData.add(newSet)
    }*/

    val outputs: ArrayList<Double>
        get() = network.outputs
}
