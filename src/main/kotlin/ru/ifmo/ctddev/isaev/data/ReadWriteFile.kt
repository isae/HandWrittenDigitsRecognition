package ru.ifmo.ctddev.isaev.data

import ru.ifmo.ctddev.isaev.neural.TrainingSet
import java.io.*
import java.util.*


object ReadWriteFile {

    fun readTrainingSets(): ArrayList<TrainingSet> {
        val trainingSets = ArrayList<TrainingSet>()

        for (i in 0..25) {
            val letterValue = (i + 65).toChar()
            val letter = letterValue.toString()
            readFromFile("/$letter.txt")
                    .mapTo(trainingSets) { TrainingSet(it, GoodOutputs.instance.getGoodOutput(letter)) }
        }

        return trainingSets
    }

    private fun readFromFile(filename: String): ArrayList<ArrayList<Int>> {
        val inputs = ArrayList<ArrayList<Int>>()

        try {
            BufferedReader(FileReader("resources" + filename)).use { reader ->
                while (true) {
                    val line = reader.readLine() ?: break
                    val input = ArrayList<Int>()
                    for (i in 0..line.length - 1) {
                        var value = 0
                        try {
                            value = Integer.parseInt(line[i].toString())
                        } catch (e: Exception) {
                        }

                        input.add(value)
                    }
                    inputs.add(input)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return inputs
    }

    fun saveToFile(input: ArrayList<Int>, filename: String) {
        try {
            val file = File("resources/$filename.txt")
            val pw = PrintWriter(FileOutputStream(file, true))
            for (i in input) {
                pw.write(Integer.toString(i))
            }
            pw.write("\n")
            pw.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
