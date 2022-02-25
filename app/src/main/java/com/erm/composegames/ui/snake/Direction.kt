package com.erm.composegames.ui.snake

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun isOpposite(newDirection: Direction) =
        (this == RIGHT && newDirection == LEFT) ||
                (this == LEFT && newDirection == RIGHT) ||
                (this == UP && newDirection == DOWN) ||
                (this == DOWN && newDirection == UP)
}