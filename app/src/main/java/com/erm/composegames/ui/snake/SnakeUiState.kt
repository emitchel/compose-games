package com.erm.composegames.ui.snake

sealed class SnakeUiState {
    object Pregame : SnakeUiState()
    data class CountDown(val countdownText: String) : SnakeUiState()
    data class GameOver(val score: Int) : SnakeUiState()

    //TODO migrate food position to SnakeState
    data class Playing(val foodPosition: Position) : SnakeUiState()
}