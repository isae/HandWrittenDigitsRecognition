package ru.ifmo.ctddev.isaev.gui.components.data

class Section(val x: Int, val y: Int, val width: Int, val height: Int) {
    var isActive: Boolean = false

    init {
        this.isActive = false
    }
}
