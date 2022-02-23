package com.erm.composegames.ui.snake

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun SnakeScreen(viewModel: SnakeViewModel = SnakeViewModel()) {

    val snakeState by viewModel.snakeState.collectAsState()
    //TODO individual cell states (thinking)
    val uiState by viewModel.uiState.collectAsState()

    viewModel.start()
    Column {
        Column(
            modifier = Modifier.height(LocalConfiguration.current.screenWidthDp.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (y in 0..9) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    for (x in 0..9) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .align(CenterVertically),
                            backgroundColor = if (snakeState.bodyPositions.contains(
                                    SnakeBodyPosition(
                                        x,
                                        y
                                    )
                                )
                            ) Color.Green else Color.Transparent
                        ) {
                            if ((uiState as? SnakeUiState.Playing)
                                    ?.foodPosition == SnakeFoodPosition(x, y)
                            ) {
                                Icon(Icons.Filled.Star, "Food")
                            }
                        }
                    }
                }
            }
        }

        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Blue)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consumeAllChanges()

                        val (x, y) = dragAmount
                        when {
                            x > 0 -> {
                                viewModel.requestDirection(SnakeDirection.RIGHT)
                            }
                            x < 0 -> {
                                viewModel.requestDirection(SnakeDirection.LEFT)
                            }
                        }
                        when {
                            y > 0 -> {
                                viewModel.requestDirection(SnakeDirection.UP)
                            }
                            y < 0 -> {
                                viewModel.requestDirection(SnakeDirection.DOWN)
                            }
                        }

                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        )
    }
}