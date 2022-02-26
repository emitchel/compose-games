package com.erm.composegames.ui.wordle

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WordleViewModel(
    lengthOfWord: Int = 5,
    numberOfAttempts: Int = 6
) : ViewModel() {
    private var _wordleState =
        MutableStateFlow(generateNewWordleState(numberOfAttempts, lengthOfWord))

    val wordleState: StateFlow<WordleState>
        get() = _wordleState.asStateFlow()

    private var _wordleUiState = MutableStateFlow(WordleUiState.Playing)
    val wordleUiState: StateFlow<WordleUiState>
        get() = _wordleUiState.asStateFlow()

    private lateinit var word: CharArray

    init {
        //TODO get word of the day from pre loaded database
        word = "joker".toCharArray()
    }

    fun input(wordleKey: WordleKeys) {
        when (wordleKey) {
            WordleKeys.Delete -> TODO()
            WordleKeys.Enter -> TODO()
            is WordleKeys.WordleLetter.Gray -> TODO()
            is WordleKeys.WordleLetter.Green -> TODO()
            is WordleKeys.WordleLetter.Invalid -> TODO()
            is WordleKeys.WordleLetter.Pending -> TODO()
            is WordleKeys.WordleLetter.Yellow -> TODO()
        }
    }

    fun restartAttempts() {
        //TODO same word, new attempts
    }

    fun newGame() {
        //TODO new word, new attempts
    }

    private fun generateNewWordleState(
        numberOfAttempts: Int,
        lengthOfWord: Int
    ) = WordleState(WordleAttempt.create(numberOfAttempts, lengthOfWord))
}