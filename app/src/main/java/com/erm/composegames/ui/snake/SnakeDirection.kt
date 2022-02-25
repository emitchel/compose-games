package com.erm.composegames.ui.snake

enum class SnakeDirection {
    UP, DOWN, LEFT, RIGHT;

    fun isOpposite(newDirection: SnakeDirection) =
        (this == RIGHT && newDirection == LEFT) ||
                (this == LEFT && newDirection == RIGHT) ||
                (this == UP && newDirection == DOWN) ||
                (this == DOWN && newDirection == UP)
}