package com.erm.composegames.ui.snake

import timber.log.Timber

data class SnakeState(
    val direction: SnakeDirection,
    val bodyPositions: List<Position>,
    val moveResult: SnakeMoveResult = SnakeMoveResult.OK
) {
    companion object {
        fun randomizeStartingPosition(snakeStartingSize: Int, gridSize: Int): SnakeState {
            //Verify the starting position and tail has enough room
            val head = Position.random(gridSize)
            Timber.d("Starting head position $head")
            //Regardless of where the tail starts, let the head go in a direction
            // with the most room
            var preferredDirection = SnakeDirection.UP
            var preferredDirectionRoomToMove = head.y

            when {
                head.x > preferredDirectionRoomToMove -> {
                    preferredDirection = SnakeDirection.LEFT
                    preferredDirectionRoomToMove = head.x
                }
                (gridSize - head.x) > preferredDirectionRoomToMove -> {
                    preferredDirection = SnakeDirection.RIGHT
                    preferredDirectionRoomToMove = (gridSize - head.x)
                }
                (gridSize - head.y) > preferredDirectionRoomToMove -> {
                    preferredDirection = SnakeDirection.DOWN
                    preferredDirectionRoomToMove = (gridSize - head.y)
                }
            }

            Timber.d("Preferred direction to move $preferredDirection, with $preferredDirectionRoomToMove spaces to move")

            //build the tail "behind" the head
            val body = when (preferredDirection) {
                SnakeDirection.UP -> (head.y until head.y + snakeStartingSize).map {
                    Position(
                        head.x,
                        it
                    )
                }
                SnakeDirection.DOWN -> ((head.y - snakeStartingSize) + 1..head.y).reversed().map {
                    Position(
                        head.x,
                        it
                    )
                }
                SnakeDirection.LEFT -> (head.x until head.x + snakeStartingSize).map {
                    Position(
                        it,
                        head.y
                    )
                }
                SnakeDirection.RIGHT -> ((head.x - snakeStartingSize) + 1..head.x).reversed().map {
                    Position(
                        it,
                        head.y
                    )
                }
            }

            return SnakeState(preferredDirection, body).also {
                Timber.d("Randomized starting snake position ${body.map { "[${it.x}, ${it.y}]" }}")
                if (body.isEmpty()) {
                    Timber.d("uh")
                }
            }
        }
    }
}