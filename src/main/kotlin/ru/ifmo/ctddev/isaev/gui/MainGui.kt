package ru.ifmo.ctddev.isaev.gui

import ru.ifmo.ctddev.isaev.algorithm.*
import ru.ifmo.ctddev.isaev.gui.components.DrawingPanel
import java.awt.Color
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.util.*
import javax.swing.*

class MainGui : JFrame("Digit recognition using neural network") {
    private val RANDOM = Random()
    private val TRAIN_SIZE = 50
    private val RESOLUTION = 28
    private val dataset = readDataSet()
    private var isPretrained = false
    private val network = NewNetwork(784, 50, 10, dataset.subList(0, TRAIN_SIZE), true)
    private val pretrainedNetwork = PretrainedNetwork(784, 50, 10, "1498159394691")
    private var networkToUse: NeuralNetwork = network

    private var mainPanel: JPanel = JPanel()
    private var drawingPanel: DrawingPanel = DrawingPanel(420, 420, RESOLUTION)

    private var clearButton: JButton = JButton("Clear")
    private var drawTrainObjectButton: JButton = JButton("Draw random train object")
    private var transformButton: JButton = JButton(">>")
    private var switchToTrainedNetworkButton: JButton = JButton("Switch to pretrained")
    private var recognizeButton: JButton = JButton("Recognize")
    private var drawLetterButton: JButton = JButton("Draw:")
    private var resultField: JTextField = JFormattedTextField("                 ")
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
        resultField.minimumSize = Dimension(180, 30)
        centerPanel.add(resultField, gbc)
        centerPanel.add(recognizeButton, gbc)

        centerPanel.add(Box.createVerticalStrut(50))

        centerPanel.add(clearButton, gbc)

        centerPanel.add(drawTrainObjectButton, gbc)
        centerPanel.add(switchToTrainedNetworkButton, gbc)
/*
        centerPanel.add(Box.createVerticalStrut(50))

        centerPanel.add(transformButton, gbc)

        centerPanel.add(Box.createVerticalStrut(50))

        clearButton.alignmentX = Component.CENTER_ALIGNMENT
        centerPanel.add(clearButton, gbc)

        centerPanel.add(Box.createVerticalStrut(50))

        centerPanel.add(JLabel("Train as:", SwingConstants.CENTER), gbc)

        centerPanel.add(drawTrainObjectButton, gbc)
*/
        mainPanel.add(centerPanel)
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

        drawTrainObjectButton.addActionListener {
            drawTrainObject(dataset[RANDOM.nextInt(dataset.size)])
        }

        transformButton.addActionListener { e ->
            // network.setInputs(drawingPanel.pixels)

            //val outputs = network.outputs
            var index = 0

            updateTextArea()

            //resultPanel.draw(GoodPixels.instance.getGoodPixels(index))
        }


        switchToTrainedNetworkButton.addActionListener {
            if (isPretrained) {
                networkToUse = pretrainedNetwork
                switchToTrainedNetworkButton.text = "Switch to pre-trained NN"
            } else {
                networkToUse = network
                switchToTrainedNetworkButton.text = "Switch to fresh NN"
            }
            isPretrained = !isPretrained
        }

        recognizeButton.addActionListener {
            val pixels = drawingPanel.pixels
                    .map { it * 255 }
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