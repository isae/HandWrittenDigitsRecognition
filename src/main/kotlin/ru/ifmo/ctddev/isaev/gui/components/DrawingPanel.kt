package ru.ifmo.ctddev.isaev.gui.components

import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.SwingUtilities

class DrawingPanel(w: Int, h: Int, count: Int) : CustomPanel(w, h, count), MouseMotionListener, MouseListener {

    init {

        addMouseMotionListener(this)
        addMouseListener(this)
    }

    override fun mouseDragged(e: MouseEvent) {
        paintSections(e)
    }

    override fun mouseMoved(e: MouseEvent) {}

    override fun mouseClicked(e: MouseEvent) {
        paintSections(e)
    }

    override fun mousePressed(e: MouseEvent) {}

    override fun mouseReleased(e: MouseEvent) {}

    override fun mouseEntered(e: MouseEvent) {}

    override fun mouseExited(e: MouseEvent) {}

    private fun paintSections(e: MouseEvent) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            sections
                    .filter { e.x > it.x && e.x < it.x + it.width && e.y > it.y && e.y < it.y + it.height }
                    .forEach { it.isActive = true }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            sections
                    .filter { e.x > it.x && e.x < it.x + it.width && e.y > it.y && e.y < it.y + it.height }
                    .forEach { it.isActive = false }
        }

        repaint()
    }
}
