package com.erm.composegames.ui.wordle

sealed class WordleGameState {
    object Playing : WordleGameState()
    object PlayingExtraAttempts : WordleGameState()
    data class Success(val word: String, val attempt: Int, val totalAttempts: Int) : WordleGameState()
    data class Fail(val word: String) : WordleGameState()
}