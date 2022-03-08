package com.erm.composegames.ui.snake

import timber.log.Timber

data class SnakeUiState(
    val direction: Direction,
    val bodyPositions: List<Position>,
    val moveResult: SnakeMoveResult = SnakeMoveResult.OK
) {
    companion object {
        fun randomizeStartingPosition(snakeStartingSize: Int, gridSize: Int): SnakeUiState {
            //Verify the starting position and tail has enough room
            val head = Position.random(gridSize)
            Timber.d("Starting head position $head")
            //Regardless of where the tail starts, let the head go in a direction
            // with the most room
            var preferredDirection = Direction.UP
            var preferredDirectionRoomToMove = head.y

            when {
                head.x > preferredDirectionRoomToMove -> {
                    preferredDirection = Direction.LEFT
                    preferredDirectionRoomToMove = head.x
                }
                (gridSize - head.x) > preferredDirectionRoomToMove -> {
                    preferredDirection = Direction.RIGHT
                    preferredDirectionRoomToMove = (gridSize - head.x)
                }
                (gridSize - head.y) > preferredDirectionRoomToMove -> {
                    preferredDirection = Direction.DOWN
                    preferredDirectionRoomToMove = (gridSize - head.y)
                }
            }

            Timber.d("Preferred direction to move $preferredDirection, with $preferredDirectionRoomToMove spaces to move")

            //build the tail "behind" the head
            val body = when (preferredDirection) {
                Direction.UP -> (head.y until head.y + snakeStartingSize).map {
                    Position(
                        head.x,
                        it
                    )
                }
                Direction.DOWN -> ((head.y - snakeStartingSize) + 1..head.y).reversed().map {
                    Position(
                        head.x,
                        it
                    )
                }
                Direction.LEFT -> (head.x until head.x + snakeStartingSize).map {
                    Position(
                        it,
                        head.y
                    )
                }
                Direction.RIGHT -> ((head.x - snakeStartingSize) + 1..head.x).reversed().map {
                    Position(
                        it,
                        head.y
                    )
                }
            }

            return SnakeUiState(preferredDirection, body).also {
                Timber.d("Randomized starting snake position ${body.map { "[${it.x}, ${it.y}]" }}")
                if (body.isEmpty()) {
                    Timber.d("uh")
                }
            }
        }
    }
}