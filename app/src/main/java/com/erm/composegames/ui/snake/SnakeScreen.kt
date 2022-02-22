package com.erm.composegames.ui.snake

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun SnakeScreen() {
    //TODO rig up settings
    val stepsPerSecond by remember { mutableStateOf(1f) }

    val state by remember { mutableStateOf(SnakeState()) }

    //TODO gesture detector...

    //TODO MIGRATE TO LAZY GRID https://alexzh.com/jetpack-compose-building-grids/
    Column {
        for (y in 0..9) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxSize()
                    .weight(1f)
            ) {
                for (x in 0..9) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .align(CenterVertically)
                            .border(width = 4.dp, color = MaterialTheme.colors.primary),
                        contentAlignment = Center
                    ) {
//                        if (state.bodyPositions.contains(SnakeBodyPosition(x, y))) {
                            Text("X")
//                        }
                    }
                }
            }
        }
    }
}

/**
 * TODO move this to view model and test!!
 *
 * Progress the snake state to the next position
 *
 * Check [SnakeState.moveResult] to determine what the UI should do
 */
fun SnakeState.progress(
    requestedDirection: SnakeDirection,
    foodPosition: SnakeFoodPosition,
    xMax: Int = 9,
    yMax: Int = 9
): SnakeState = run {

    //Don't update direction if it's the opposite
    val directionAdjustFor =
        if (direction.isOpposite(requestedDirection)) direction else requestedDirection

    val head = bodyPositions.first()
    val nextPosition = when (directionAdjustFor) {
        SnakeDirection.UP -> head.copy(x = head.x, y = head.y + 1)
        SnakeDirection.DOWN -> head.copy(x = head.x, y = head.y - 1)
        SnakeDirection.LEFT -> head.copy(x = head.x - 1, y = head.y)
        SnakeDirection.RIGHT -> head.copy(x = head.x + 1, y = head.y)
    }

    //Move body right away
    var newBody = (listOf(nextPosition) + bodyPositions).toMutableList()

    //If the LAST STATE wasn't ATE then remove the tail before evaluating next result
    if (moveResult != SnakeMoveResult.ATE) {
        newBody.removeLast()
    }

    //Snake can't move out of bounds
    if (nextPosition.x > xMax || nextPosition.y > yMax || listOf(
            nextPosition.x,
            nextPosition.y
        ).any { it < 0 }
    ) return copy(
        bodyPositions = newBody,
        direction = directionAdjustFor,
        moveResult = SnakeMoveResult.OB
    )

    //Snake clashed with food, set result to ATE
    if (nextPosition.x == foodPosition.x && nextPosition.y == foodPosition.y)
        return copy(
            bodyPositions = newBody,
            direction = directionAdjustFor,
            moveResult = SnakeMoveResult.ATE
        )

    //Snake clashed with itself, set result to CLASH
    if (bodyPositions.contains(nextPosition))
        return copy(
            bodyPositions = newBody,
            direction = directionAdjustFor,
            moveResult = SnakeMoveResult.CLASH
        )

    //Normal move otherwise
    return copy(
        bodyPositions = newBody,
        direction = directionAdjustFor,
        moveResult = SnakeMoveResult.OK
    )
}

data class SnakeState(
    val direction: SnakeDirection = SnakeDirection.RIGHT,
    val bodyPositions: List<SnakeBodyPosition> = listOf(SnakeBodyPosition(5, 5)),
    val moveResult: SnakeMoveResult = SnakeMoveResult.OK
)

enum class SnakeDirection {
    UP, DOWN, LEFT, RIGHT;

    fun isOpposite(newDirection: SnakeDirection) =
        (this == RIGHT && newDirection == LEFT) ||
                (this == LEFT && newDirection == RIGHT) ||
                (this == UP && newDirection == DOWN) ||
                (this == DOWN && newDirection == UP)
}

enum class SnakeMoveResult {
    OK, //Moved in a valid position
    ATE, //Ate a fruit
    OB, //Moved out of bounds
    CLASH //Clashed with a part of the snak
}

data class SnakeBodyPosition(
    val x: Int,
    val y: Int
)

data class SnakeFoodPosition(val x: Int, val y: Int)