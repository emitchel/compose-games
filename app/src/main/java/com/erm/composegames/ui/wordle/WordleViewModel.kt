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

    private var _wordleUiState = MutableStateFlow<WordleUiState>(WordleUiState.Playing)
    val wordleUiState: StateFlow<WordleUiState>
        get() = _wordleUiState.asStateFlow()

    private var currentAttemptIndex = 0
    private val currentWordleAttempt: WordleAttempt get() = _wordleState.value.attempts[currentAttemptIndex]
    private lateinit var currentWord: CharArray

    init {
        //TODO get word of the day from pre loaded database
        currentWord = "joker".toCharArray()
    }

    fun input(wordleKey: WordleKeys) {
        when (wordleKey) {
            WordleKeys.Delete -> {
                if (currentWordleAttempt.letters.all { it.char == null }) return //no op, no letters!

                val newAttempt = WordleAttempt(currentWordleAttempt.letters.toMutableList().apply {
                    set(
                        currentWordleAttempt.letters.indexOfLast { it.char != null },
                        WordleKeys.WordleLetter.Pending(null)
                    )
                })
                updateCurrentAttempt(newAttempt)
            }
            WordleKeys.Enter -> {
                if (currentWordleAttempt.letters.any { it.char == null }) return //no op, no filled out yet!

                //TODO implement is valid word boolean (read from dao?)
                if ((0..10).random() > 9) {
                    //Is NOT valid word
                    val newAttempt = WordleAttempt(currentWordleAttempt.letters.map {
                        WordleKeys.WordleLetter.Invalid(it.char!!)
                    })
                    updateCurrentAttempt(newAttempt)
                } else {
                    //Is valid word
                    val newAttempt =
                        WordleAttempt(currentWordleAttempt.letters.mapIndexed { index, wordleLetter ->
                            when {
                                currentWord[index] == wordleLetter.char -> {
                                    WordleKeys.WordleLetter.Green(wordleLetter.char!!)
                                }
                                currentWord.contains(wordleLetter.char!!) -> {
                                    WordleKeys.WordleLetter.Yellow(wordleLetter.char!!)
                                }
                                else -> {
                                    WordleKeys.WordleLetter.Gray(wordleLetter.char!!)
                                }
                            }
                        })
                    updateCurrentAttempt(newAttempt)
                    updateKeyboardWithCurrentAttempts()
                    updateToNextAttempt(newAttempt)
                }
            }
            is WordleKeys.WordleLetter -> wordleKey.char?.let { enteredChar ->
                val indexOfLetterToSet =
                    currentWordleAttempt.letters.indexOfFirst { it.char == null }
                if (indexOfLetterToSet == -1) return //no op, row filled out!

                //Update the letter in attempt row
                // e.x. [A* _ _ _ _]
                val newAttempt = WordleAttempt(currentWordleAttempt.letters.toMutableList()
                    .apply {
                        set(indexOfLetterToSet, enteredChar.toPending())
                    })

                //Update attempt row in wordle state
                // e.x. [A B C D E]
                //      [A _ _ _ _]*
                updateCurrentAttempt(newAttempt)
            }
        }
    }

    private fun updateToNextAttempt(lastAttemptAdded: WordleAttempt) {
        if (lastAttemptAdded.letters.all { it is WordleKeys.WordleLetter.Green }) {
            //Success!
            _wordleUiState.value = WordleUiState.Success(
                currentWord.toString(),
                currentAttemptIndex + 1,
                wordleState.value.attempts.size
            )
            return
        }
        currentAttemptIndex++ //Update index
        if (currentAttemptIndex == wordleState.value.attempts.size) {
            //If no index is at the max attempts, show failure :( 
            _wordleUiState.value = WordleUiState.Fail(currentWord.toString())
        }
    }

    //Not sure if this could be any more inefficient
    private fun updateKeyboardWithCurrentAttempts() {
        val yellows = _wordleState.value.attempts.flatMap { it.letters }
            .filterIsInstance<WordleKeys.WordleLetter.Yellow>().map { it.char }
        val greens = _wordleState.value.attempts.flatMap { it.letters }
            .filterIsInstance<WordleKeys.WordleLetter.Green>().map { it.char }

        val keyboardRows = _wordleState.value.keyboard.keys.map {
            it.map { key ->
                when (key) {
                    is WordleKeys.WordleLetter -> {
                        if (yellows.contains(key.char)) {
                            WordleKeys.WordleLetter.Yellow(key.char!!)
                        } else if (greens.contains(key.char)) {
                            WordleKeys.WordleLetter.Green(key.char!!)
                        } else {
                            WordleKeys.WordleLetter.Gray(key.char!!)
                        }
                    }
                    else -> key //keep references to enter and delete
                }
            }
        }

        _wordleState.value = _wordleState.value.copy(
            keyboard = WordleKeyboard(keyboardRows)
        )

    }

    private fun updateCurrentAttempt(newAttempt: WordleAttempt) {
        _wordleState.value = _wordleState.value.run {
            val newAttempts = attempts.toMutableList().apply {
                set(currentAttemptIndex, newAttempt)
            }

            copy(attempts = newAttempts)
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