package com.erm.composegames.ui.snake

data class Position(
    val x: Int,
    val y: Int
) {

    override fun toString() = "Position[$x, $y]"

    companion object {
        fun random(size: Int) = Position((0 until size).random(), (0 until size).random())
    }
}
