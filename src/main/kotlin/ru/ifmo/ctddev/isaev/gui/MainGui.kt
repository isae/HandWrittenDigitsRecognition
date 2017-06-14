package ru.ifmo.ctddev.isaev.gui

import ru.ifmo.ctddev.isaev.data.GoodOutputs
import ru.ifmo.ctddev.isaev.data.GoodPixels
import ru.ifmo.ctddev.isaev.data.ReadWriteFile
import ru.ifmo.ctddev.isaev.gui.components.CustomPanel
import ru.ifmo.ctddev.isaev.gui.components.DrawingPanel
import ru.ifmo.ctddev.isaev.neural.Train
import ru.ifmo.ctddev.isaev.neural.TrainingSet
import java.awt.*
import javax.swing.*

class MainGui : JFrame("Drawing letters using ru.ifmo.ctddev.isaev.neural networks") {

    private val RESOLUTION = 20

    private val networkTrainer: Train = Train()

    private var mainPanel: JPanel? = null
    private var drawingPanel: DrawingPanel? = null
    private var resultPanel: CustomPanel? = null

    private var clearButton: JButton? = null
    private var trainButton: JButton? = null
    private var transformButton: JButton? = null
    private var helpButton: JButton? = null
    private var trainNetworkButton: JButton? = null
    private var drawLetterButton: JButton? = null
    private var trainingSetsAmount: JTextField? = null
    private var drawLetterCombo: JComboBox<String>? = null
    private var trainAsCombo: JComboBox<String>? = null
    private var outputTextArea: JTextArea? = null

    init {

        setMainPanel()
        setLeftSide()
        setCenterArea()
        setRightSide()
        setOutputPanel()

        setOnClicks()

        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        isVisible = true
        size = Dimension(1260, 500)
        setLocationRelativeTo(null)
        isResizable = false
    }

    private fun setMainPanel() {
        mainPanel = JPanel()
        mainPanel!!.background = Color.LIGHT_GRAY
        contentPane = mainPanel
    }

    private fun setLeftSide() {
        val panel = JPanel()
        panel.background = Color.LIGHT_GRAY
        panel.preferredSize = Dimension(410, 440)

        drawLetterButton = JButton("Draw:")
        drawLetterCombo = JComboBox(arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Z", "Y"))

        drawingPanel = DrawingPanel(400, 400, RESOLUTION)

        panel.add(drawLetterButton)
        panel.add(drawLetterCombo)
        panel.add(drawingPanel)

        mainPanel!!.add(panel)
    }

    private fun setCenterArea() {
        val centerPanel = JPanel()
        centerPanel.layout = GridBagLayout()
        centerPanel.preferredSize = Dimension(200, 400)
        val gbc = GridBagConstraints()
        gbc.gridwidth = GridBagConstraints.REMAINDER
        gbc.anchor = GridBagConstraints.CENTER

        trainNetworkButton = JButton("Train X times:")
        trainingSetsAmount = JFormattedTextField("5000")
        trainingSetsAmount!!.maximumSize = Dimension(100, 30)
        trainingSetsAmount!!.preferredSize = Dimension(100, 30)
        centerPanel.add(trainNetworkButton!!, gbc)
        centerPanel.add(trainingSetsAmount!!, gbc)

        centerPanel.add(Box.createVerticalStrut(50))

        helpButton = JButton("HELP")
        centerPanel.add(helpButton!!, gbc)

        centerPanel.add(Box.createVerticalStrut(50))

        transformButton = JButton(">>")
        centerPanel.add(transformButton!!, gbc)

        centerPanel.add(Box.createVerticalStrut(50))

        clearButton = JButton("Clear")
        clearButton!!.alignmentX = Component.CENTER_ALIGNMENT
        centerPanel.add(clearButton!!, gbc)

        centerPanel.add(Box.createVerticalStrut(50))

        centerPanel.add(JLabel("Train as:", SwingConstants.CENTER), gbc)

        trainAsCombo = JComboBox(arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Z", "Y"))
        trainAsCombo!!.alignmentX = Component.CENTER_ALIGNMENT
        trainAsCombo!!.maximumSize = Dimension(trainAsCombo!!.preferredSize.width, 30)
        centerPanel.add(trainAsCombo!!, gbc)

        trainButton = JButton("Train")
        centerPanel.add(trainButton!!, gbc)

        mainPanel!!.add(centerPanel)
    }

    private fun setRightSide() {
        val panel = JPanel()
        panel.background = Color.LIGHT_GRAY
        panel.border = BorderFactory.createEmptyBorder(30, 0, 0, 0)
        resultPanel = CustomPanel(400, 400, RESOLUTION)
        panel.add(resultPanel)
        mainPanel!!.add(panel)
    }

    private fun setOutputPanel() {
        val outputPanel = JPanel()
        outputPanel.preferredSize = Dimension(200, 430)

        outputTextArea = JTextArea()
        outputTextArea!!.preferredSize = Dimension(200, 430)
        outputPanel.add(outputTextArea)

        mainPanel!!.add(outputPanel)
    }

    private fun setOnClicks() {
        clearButton!!.addActionListener { drawingPanel!!.clear() }

        trainButton!!.addActionListener {
            val letter = trainAsCombo!!.selectedItem as String
            networkTrainer.addTrainingSet(TrainingSet(drawingPanel!!.pixels, GoodOutputs.instance.getGoodOutput(letter)))
            ReadWriteFile.saveToFile(drawingPanel!!.pixels, letter)
        }

        transformButton!!.addActionListener { e ->
            networkTrainer.setInputs(drawingPanel!!.pixels)

            val outputs = networkTrainer.outputs
            var index = 0
            outputs.indices
                    .asSequence()
                    .filter { outputs[it] > outputs[index] }
                    .forEach { index = it }

            updateTextArea()

            trainAsCombo!!.selectedIndex = index
            resultPanel!!.drawLetter(GoodPixels.instance.getGoodPixels(index))
        }


        helpButton!!.addActionListener { e ->
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

        trainNetworkButton!!.addActionListener { e ->
            var number = 0
            try {
                number = Integer.parseInt(trainingSetsAmount!!.text)
            } catch (x: Exception) {
                JOptionPane.showMessageDialog(this, "Wrong input", "ERROR", JOptionPane.PLAIN_MESSAGE)
            }

            networkTrainer.train(number.toLong())
        }

        drawLetterButton!!.addActionListener { e ->
            val letter = drawLetterCombo!!.selectedItem as String
            val goodPixels = GoodPixels.instance.getGoodPixels(letter)
            drawingPanel!!.drawLetter(goodPixels)
        }

        drawLetterCombo!!.addActionListener { e ->
            val letter = drawLetterCombo!!.selectedItem as String
            val goodPixels = GoodPixels.instance.getGoodPixels(letter)
            drawingPanel!!.drawLetter(goodPixels)
        }

    }

    private fun updateTextArea() {
        val sb = StringBuilder()
        val outputs = networkTrainer.outputs
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
        }
        outputTextArea!!.text = sb.toString()
    }

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            MainGui()
        }
    }

}
