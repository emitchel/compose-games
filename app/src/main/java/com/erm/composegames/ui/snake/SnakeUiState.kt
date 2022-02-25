package com.erm.composegames.ui.snake

sealed class SnakeUiState {
    object Pregame : SnakeUiState()
    data class CountDown(val countdownText: String) : SnakeUiState()
    data class Playing(val foodPosition: Position) : SnakeUiState()
    data class GameOver(val score: Int) : SnakeUiState()
}