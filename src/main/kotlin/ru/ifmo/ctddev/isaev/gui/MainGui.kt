package ru.ifmo.ctddev.isaev.gui

import ru.ifmo.ctddev.isaev.algorithm.NeuralNetwork
import ru.ifmo.ctddev.isaev.algorithm.TrainObject
import ru.ifmo.ctddev.isaev.algorithm.readDataSet
import ru.ifmo.ctddev.isaev.gui.components.CustomPanel
import ru.ifmo.ctddev.isaev.gui.components.DrawingPanel
import java.awt.Color
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

class MainGui : JFrame("Digit recognition using neural network") {

    private val RESOLUTION = 28
    private val network = NeuralNetwork(784, 50, 10, readDataSet())

    private var mainPanel: JPanel = JPanel()
    private var drawingPanel: DrawingPanel = DrawingPanel(400, 400, RESOLUTION)
    private var resultPanel: CustomPanel = CustomPanel(400, 400, RESOLUTION)

    private var clearButton: JButton = JButton("Clear")
    private var trainButton: JButton = JButton("Train")
    private var transformButton: JButton = JButton(">>")
    private var helpButton: JButton = JButton("HELP")
    private var recognizeButton: JButton = JButton("Recognize")
    private var drawLetterButton: JButton = JButton("Draw:")
    private var resultField: JTextField = JFormattedTextField("5000")
    private var outputTextArea: JTextArea = JTextArea()

    init {

        setMainPanel()
        setLeftSide()
        setCenterArea()
        //setRightSide()
        //setOutputPanel()

        setOnClicks()

        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        isVisible = true
        size = Dimension(630, 500)
        setLocationRelativeTo(null)
        isResizable = false
        //drawTrainObject(network.trainData[239])
    }

    private fun drawTrainObject(trainObject: TrainObject) {
        val avg = trainObject.data.average()
        drawingPanel.draw(trainObject.data.map { if (it > avg) it.toInt() else 0 }.toIntArray())
    }

    private fun setMainPanel() {
        mainPanel.background = Color.LIGHT_GRAY
        contentPane = mainPanel
    }

    private fun setLeftSide() {
        val panel = JPanel()
        panel.background = Color.LIGHT_GRAY
        panel.preferredSize = Dimension(410, 440)

        //panel.add(drawLetterButton)
        panel.add(drawingPanel)

        mainPanel.add(panel)
    }

    private fun setCenterArea() {
        val centerPanel = JPanel()
        centerPanel.layout = GridBagLayout()
        centerPanel.preferredSize = Dimension(200, 400)
        val gbc = GridBagConstraints()
        gbc.gridwidth = GridBagConstraints.REMAINDER
        gbc.anchor = GridBagConstraints.CENTER

        resultField.maximumSize = Dimension(100, 30)
        resultField.preferredSize = Dimension(100, 30)
        centerPanel.add(recognizeButton, gbc)
        centerPanel.add(resultField, gbc)

        centerPanel.add(Box.createVerticalStrut(50))

        centerPanel.add(clearButton, gbc)
/*
        centerPanel.add(Box.createVerticalStrut(50))

        centerPanel.add(transformButton, gbc)

        centerPanel.add(Box.createVerticalStrut(50))

        clearButton.alignmentX = Component.CENTER_ALIGNMENT
        centerPanel.add(clearButton, gbc)

        centerPanel.add(Box.createVerticalStrut(50))

        centerPanel.add(JLabel("Train as:", SwingConstants.CENTER), gbc)

        centerPanel.add(trainButton, gbc)
*/
        mainPanel.add(centerPanel)
    }

    private fun setRightSide() {
        val panel = JPanel()
        panel.background = Color.LIGHT_GRAY
        panel.border = BorderFactory.createEmptyBorder(30, 0, 0, 0)
        panel.add(resultPanel)
        mainPanel.add(panel)
    }

    private fun setOutputPanel() {
        val outputPanel = JPanel()
        outputPanel.preferredSize = Dimension(200, 430)

        outputTextArea.preferredSize = Dimension(200, 430)
        outputPanel.add(outputTextArea)

        mainPanel.add(outputPanel)
    }

    private fun setOnClicks() {
        clearButton.addActionListener { drawingPanel.clear() }

        trainButton.addActionListener {
            //network.addTrainingSet(TrainingSet(drawingPanel.pixels, GoodOutputs.instance.getGoodOutput(letter)))
            //ReadWriteFile.saveToFile(drawingPanel.pixels, letter)
        }

        transformButton.addActionListener { e ->
            // network.setInputs(drawingPanel.pixels)

            //val outputs = network.outputs
            var index = 0

            updateTextArea()

            //resultPanel.draw(GoodPixels.instance.getGoodPixels(index))
        }


        helpButton.addActionListener { e ->
            val sb = StringBuilder()
            sb.append("Train network X times after you start the program. Recommended 5000 times\n")
            sb.append("\n")
            sb.append("Use left/right mouse button to draw/erase\n")
            sb.append("\n")
            sb.append("Click \">>\" to see result\n")
            sb.append("\n")
            sb.append("Click \"Train\" to train specific letter\n")
            JOptionPane.showMessageDialog(this, sb.toString(), "Help", JOptionPane.PLAIN_MESSAGE)
        }

        recognizeButton.addActionListener {
            val pixels = drawingPanel.pixels
                    .map { it*255 }
                    .map { it.toDouble() }
                    .toDoubleArray()
            resultField.text = "Recognized digit: ${network.predictResult(pixels)}"
        }

        drawLetterButton.addActionListener {
            //val goodPixels = GoodPixels.instance.getGoodPixels(letter)
            //drawingPanel.draw(goodPixels)
        }

    }

    private fun updateTextArea() {
        val sb = StringBuilder()
        /* val outputs = network.outputs
         for (i in outputs.indices) {
             val letterValue = i + 65
             sb.append(letterValue.toChar())
             var value = outputs[i]
             if (value < 0.01)
                 value = 0.0
             if (value > 0.99)
                 value = 1.0
 
             value *= 1000.0
             val x = value.toInt()
             value = x / 1000.0
 
             sb.append("\t " + value)
             sb.append("\n")
         }*/
        outputTextArea.text = sb.toString()
    }
}

fun main(args: Array<String>) {
    MainGui()
}