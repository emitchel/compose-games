package com.erm.composegames.ui.wordle


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.internal.isLiveLiteralsEnabled
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erm.composegames.ui.theme.InvalidWord
import com.erm.composegames.ui.theme.Pending
import com.erm.composegames.ui.wordle.WordleKey.WordleLetter.Companion.isEnteredValidWord
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Preview
@Composable
fun WordleScreen() {
    val viewModel: WordleViewModel = viewModel(
        factory = WordleViewModel.Factory.Create(
            numberOfAttempts = 6,
            lengthOfWord = 5
        )
    )

    val uiState by viewModel.wordleUiState.collectAsState()
    //TODO rig up game state
    val gameState by viewModel.wordleGameState.collectAsState()
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        WordleGrid(uiState.attempts)
        WordleKeyboard(uiState.keyboard) {
            viewModel.input(it)
        }
    }
}

@Composable
private fun WordleKeyboard(
    keyboard: WordleKeyboard,
    keyPressed: (WordleKey) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        keyboard.keys.forEach { keyRow ->
            Row(
                modifier = Modifier
                    .height(52.dp)
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                keyRow.forEach { key ->
                    Box(
                        modifier = Modifier
                            .border(1.dp, Black)
                            .run {
                                if (key is WordleKey.WordleFunctionKey) {
                                    width(64.dp)
                                } else wrapContentWidth()
                            }
                            .fillMaxHeight()
                            .defaultMinSize(minWidth = 32.dp)
                            .align(Alignment.CenterVertically)
                            .clickable { keyPressed(key) }
                            .background(
                                if (key is WordleKey.WordleLetter) key.color else White
                            )
                    ) {
                        Text(
                            text = key.text.orEmpty(),
                            fontSize = if (key is WordleKey.WordleLetter) 16.sp else 10.sp,
                            color = if (key is WordleKey.WordleLetter.Pending || key !is WordleKey.WordleLetter) Black else White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.h2
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WordleGrid(
    wordleAttempts: List<WordleAttempt>
) {
    Column(
        modifier = Modifier.padding(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        wordleAttempts.forEach { attempt ->
            Row(
                modifier = Modifier
                    .wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                attempt.letters.forEach { letter ->
                    val rotation: Float by animateFloatAsState(
                        targetValue = if (letter.isEnteredValidWord()) 180f else 0f,
                        animationSpec = tween(durationMillis = 600)
                    )

                    var shiftX by remember { mutableStateOf(0f) }
                    val shiftAnimation: Float by animateFloatAsState(
                        targetValue = shiftX,
                        animationSpec = tween(durationMillis = 300)
                    )
                    if (letter is WordleKey.WordleLetter.InvalidWord) {
                        LaunchedEffect(Unit) {
                            repeat(3) {
                                if (shiftX == 0f) shiftX = 10f
                                delay(150)
                                shiftX *= -1f
                            }
                            shiftX = 0f
                        }
                    } else {
                        shiftX = 0f
                    }

                    Box(
                        modifier = Modifier
                            .graphicsLayer(rotationX = rotation, translationX = shiftAnimation)
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(1.dp, Black)
                            .align(Alignment.CenterVertically)
                            .background(if (letter.isEnteredValidWord() && rotation > 90f) letter.color else Pending)
                    ) {
                        Text(
                            text = letter.char?.toString() ?: "",
                            color = if (letter.isEnteredValidWord() && rotation > 90f) White else Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .graphicsLayer(rotationX = if (letter.isEnteredValidWord() && rotation > 90f) 180f else 0f)
                                .align(Alignment.Center),
                            style = MaterialTheme.typography.h4
                        )
                    }
                }
            }
        }
    }
}