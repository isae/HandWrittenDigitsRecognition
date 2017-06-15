package ru.ifmo.ctddev.isaev.gui.components

import ru.ifmo.ctddev.isaev.gui.components.data.Section
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JPanel

open class CustomPanel(_width: Int,
                       _height: Int,
                       val count: Int,
                       var sections: ArrayList<Section> = ArrayList()) : JPanel() {

    init {
        setSize(_width, _height)

        preferredSize = Dimension(width, height)
        background = Color.WHITE

        generateSections()
    }

    private fun generateSections() {
        sections = ArrayList<Section>()

        for (j in 0..count - 1) {
            for (i in 0..count - 1) {
                sections.add(Section(i * (width / count), j * (height / count), width / count, height / count))
            }
        }

        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        generateSections(g)
        drawSections(g)

    }

    private fun generateSections(g: Graphics) {
        g.color = Color.LIGHT_GRAY

        for (s in sections) {
            g.drawLine(0, s.y, width, s.y)
            g.drawLine(s.x, 0, s.x, height)
        }
    }

    private fun drawSections(g: Graphics) {
        g.color = Color.BLACK
        sections
                .filter { it.isActive }
                .forEach { g.fillRect(it.x, it.y, it.width, it.height) }
    }

    val pixels: ArrayList<Int>
        get() {
            val pixels = ArrayList<Int>()
            for (s in sections) {
                if (s.isActive)
                    pixels.add(1)
                else
                    pixels.add(0)
            }

            return pixels
        }

    fun clear() {
        for (s in sections) {
            s.isActive = false
        }

        repaint()
    }

    fun draw(pixels: IntArray) {
        for (i in pixels.indices) {
            sections[i].isActive = pixels[i] > 0
        }

        repaint()
    }
}
