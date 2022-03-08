package com.erm.composegames.ui.snake

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SnakeScreen() {
    val viewModel: SnakeViewModel = viewModel(
        factory = SnakeViewModel.Factory.Create(
            gridSize = 10,
            stepsPerSecond = 1,
            snakeStartingSize = 3
        )
    )

    val snakeState by viewModel.snakeUiState.collectAsState()
    val uiState by viewModel.gameState.collectAsState()
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        SnakeGrid(
            viewModel.gridSize.value,
            snakeState.bodyPositions,
            (uiState as? SnakeGameState.Playing)?.foodPosition
        )
        when (val state = uiState) {
            is SnakeGameState.CountDown ->
                GameInfo(
                    state.countdownText
                )
            is SnakeGameState.GameOver ->
                GameInfo("Game Over! Score: ${state.score}") {
                    viewModel.start(true)
                }
            is SnakeGameState.Playing ->
                SwipeController(viewModel.snakeUiState.value.direction) {
                    viewModel.requestDirection(it)
                }
            SnakeGameState.Pregame -> Button(
                onClick = { viewModel.start() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Press to start")
            }
        }

    }
}

@Preview
@Composable
private fun GameInfo(text: String = "Game Over!", onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick?.invoke() }
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.h2
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
private fun SwipeController(currentDirection: Direction, directionCallback: (Direction) -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()

                    val (x, y) = dragAmount
                    when {
                        x > 0 -> {
                            directionCallback(Direction.RIGHT)
                        }
                        x < 0 -> {
                            directionCallback(Direction.LEFT)
                        }
                    }
                    when {
                        y > 0 -> {
                            directionCallback(Direction.DOWN)
                        }
                        y < 0 -> {
                            directionCallback(Direction.UP)
                        }
                    }

                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {

        val (up, left, right, down, text) = createRefs()

        Text(
            "Swipe",
            modifier = Modifier.constrainAs(text) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
        )
        Icon(
            modifier = Modifier.constrainAs(up) {
                top.linkTo(parent.top)
                bottom.linkTo(text.top)
                start.linkTo(text.start)
                end.linkTo(text.end)
            },
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = "Up",
            tint = if (currentDirection == Direction.UP) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
        )
        Icon(
            modifier = Modifier.constrainAs(left) {
                top.linkTo(text.top)
                end.linkTo(text.start)
                start.linkTo(parent.start)
            },
            imageVector = Icons.Filled.KeyboardArrowLeft,
            contentDescription = "Left",
            tint = if (currentDirection == Direction.LEFT) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
        )
        Icon(
            modifier = Modifier.constrainAs(right) {
                top.linkTo(text.top)
                start.linkTo(text.end)
                end.linkTo(parent.end)
            },
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = "Right",
            tint = if (currentDirection == Direction.RIGHT) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
        )
        Icon(
            modifier = Modifier.constrainAs(down) {
                top.linkTo(text.bottom)
                start.linkTo(text.start)
                end.linkTo(text.end)
                bottom.linkTo(parent.bottom)
            },
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = "Down",
            tint = if (currentDirection == Direction.DOWN) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
        )
    }
}

@Composable
private fun SnakeGrid(
    gridSize: Int,
    body: List<Position>,
    foodPosition: Position? = null
) {
    Column(
        modifier = Modifier.padding(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (y in 0 until gridSize) {
            Row(
                modifier = Modifier
                    .wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (x in 0 until gridSize) {
                    Card(
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .align(CenterVertically),
                        backgroundColor = if (body.contains(
                                Position(
                                    x,
                                    y
                                )
                            )
                        ) MaterialTheme.colors.primaryVariant else Color.Transparent
                    ) {
                        if (foodPosition == Position(x, y)) {
                            Icon(Icons.Filled.Star, "Food")
                        }
                    }
                }
            }
        }
    }
}