package com.erm.composegames.ui.wordle


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
fun WordleScreen() {
    val viewModel: WordleViewModel = viewModel(
        factory = WordleViewModel.Factory.Create(
            numberOfAttempts = 6,
            lengthOfWord = 5
        )
    )

    val wordleState by viewModel.wordleState.collectAsState()
    val uiState by viewModel.wordleUiState.collectAsState()
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        WordleGrid(wordleState.attempts)
        WordleKeyboard(wordleState.keyboard) {
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
                            .wrapContentWidth()
                            .fillMaxHeight()
                            .defaultMinSize(minWidth = 32.dp)
                            .align(Alignment.CenterVertically)
                            .clickable { keyPressed(key) }
                            .background(
                                when (key) {
                                    is WordleKey.WordleLetter.Green -> Green
                                    is WordleKey.WordleLetter.Gray -> Gray
                                    is WordleKey.WordleLetter.Yellow -> Yellow
                                    is WordleKey.WordleLetter.Pending -> White
                                    else -> White
                                }
                            )
                    ) {
                        Text(
                            text = when (key) {
                                is WordleKey.WordleLetter -> key.char.toString()
                                is WordleKey.Delete -> "Delete"
                                is WordleKey.Enter -> "Enter"
                            },
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
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(1.dp, Black)
                            .align(Alignment.CenterVertically)
                            .background(
                                when (letter) {
                                    is WordleKey.WordleLetter.Green -> Green
                                    is WordleKey.WordleLetter.Gray -> Gray
                                    is WordleKey.WordleLetter.Yellow -> Yellow
                                    is WordleKey.WordleLetter.Pending -> White
                                    is WordleKey.WordleLetter.Invalid -> Red
                                }
                            )
                    ) {
                        Text(
                            text = letter.char?.toString() ?: "",
                            color = if (letter is WordleKey.WordleLetter.Pending) Black else White,
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