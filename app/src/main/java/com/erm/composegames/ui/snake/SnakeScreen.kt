package com.erm.composegames.ui.snake

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun SnakeScreen(viewModel: SnakeViewModel = SnakeViewModel()) {

    val state by viewModel.snakeState.collectAsState()

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