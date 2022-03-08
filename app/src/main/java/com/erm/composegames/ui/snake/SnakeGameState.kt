package com.erm.composegames.ui.snake

sealed class SnakeGameState {
    object Pregame : SnakeGameState()
    data class CountDown(val countdownText: String) : SnakeGameState()
    data class GameOver(val score: Int) : SnakeGameState()
    data class Playing(val foodPosition: Position) : SnakeGameState()
}