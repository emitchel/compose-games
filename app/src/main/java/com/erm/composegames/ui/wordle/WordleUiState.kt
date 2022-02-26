package com.erm.composegames.ui.wordle

sealed class WordleUiState {
    object Playing : WordleUiState()
    object PlayingExtraAttempts : WordleUiState()
    data class Success(val word: String, val attempt: Int, val totalAttempts: Int) : WordleUiState()
    data class Fail(val word: String) : WordleUiState()
}