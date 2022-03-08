package com.erm.composegames.ui.wordle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WordleViewModel(
    private val lengthOfWord: Int = 5,
    private val numberOfAttempts: Int = 6
) : ViewModel() {
    private var _wordleUiState =
        MutableStateFlow(generateNewWordleState(numberOfAttempts, lengthOfWord))

    val wordleUiState: StateFlow<WordleUiState>
        get() = _wordleUiState.asStateFlow()

    private var _wordleGameState = MutableStateFlow<WordleGameState>(WordleGameState.Playing)
    val wordleGameState: StateFlow<WordleGameState>
        get() = _wordleGameState.asStateFlow()

    private var currentAttemptIndex = 0
    private val currentWordleAttempt: WordleAttempt get() = _wordleUiState.value.attempts[currentAttemptIndex]
    private var currentWord: CharArray = WordleRepository.getTodaysWord().toCharArray()

    fun input(wordleKey: WordleKey) {
        when (wordleKey) {
            WordleKey.Delete -> {
                if (currentWordleAttempt.letters.all { it.char == null }) return //no op, no letters!

                val newAttempt = WordleAttempt(currentWordleAttempt.letters.toMutableList().apply {
                    set(
                        currentWordleAttempt.letters.indexOfLast { it.char != null },
                        WordleKey.WordleLetter.Pending(null)
                    )
                })
                updateCurrentAttempt(newAttempt)
            }
            WordleKey.Enter -> {
                if (currentWordleAttempt.letters.any { it.char == null }) return //no op, no letters filled out yet!

                if (!WordleRepository.isValidWord(currentWordleAttempt.letters.map { it.char!! }
                        .joinToString(""))) {
                    //Is NOT valid word
                    val newAttempt = WordleAttempt(currentWordleAttempt.letters.map {
                        WordleKey.WordleLetter.InvalidWord(it.char!!)
                    })
                    updateCurrentAttempt(newAttempt)
                } else {
                    //Is valid word
                    val newAttempt =
                        WordleAttempt(currentWordleAttempt.letters.mapIndexed { index, wordleLetter ->
                            when {
                                currentWord[index] == wordleLetter.char -> {
                                    WordleKey.WordleLetter.Correct(wordleLetter.char!!)
                                }
                                currentWord.contains(wordleLetter.char!!) -> {
                                    WordleKey.WordleLetter.WrongPosition(wordleLetter.char!!)
                                }
                                else -> {
                                    WordleKey.WordleLetter.Absent(wordleLetter.char!!)
                                }
                            }
                        })
                    updateCurrentAttempt(newAttempt)
                    updateKeyboardWithCurrentAttempts()
                    updateToNextAttempt(newAttempt)
                }
            }
            is WordleKey.WordleLetter -> wordleKey.char?.let { enteredChar ->
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
        if (lastAttemptAdded.letters.all { it is WordleKey.WordleLetter.Correct }) {
            //Success!
            _wordleGameState.value = WordleGameState.Success(
                currentWord.toString(),
                currentAttemptIndex + 1,
                numberOfAttempts
            )
            return
        }
        currentAttemptIndex++ //Update index
        if (currentAttemptIndex == numberOfAttempts) {
            //If no index is at the max attempts, show failure :(
            _wordleGameState.value = WordleGameState.Fail(currentWord.toString())
        }
    }

    //Not sure if this could be any more inefficient
    private fun updateKeyboardWithCurrentAttempts() {
        val yellows = _wordleUiState.value.attempts.flatMap { it.letters }
            .filterIsInstance<WordleKey.WordleLetter.WrongPosition>().map { it.char }
        val greens = _wordleUiState.value.attempts.flatMap { it.letters }
            .filterIsInstance<WordleKey.WordleLetter.Correct>().map { it.char }
        val grays = _wordleUiState.value.attempts.flatMap { it.letters }
            .filterIsInstance<WordleKey.WordleLetter.Absent>().map { it.char }

        val keyboardRows = _wordleUiState.value.keyboard.keys.map {
            it.map { key ->
                when (key) {
                    is WordleKey.WordleLetter -> {
                        if (yellows.contains(key.char)) {
                            WordleKey.WordleLetter.WrongPosition(key.char!!)
                        } else if (greens.contains(key.char)) {
                            WordleKey.WordleLetter.Correct(key.char!!)
                        } else if (grays.contains(key.char)) {
                            WordleKey.WordleLetter.Absent(key.char!!)
                        } else {
                            WordleKey.WordleLetter.Pending(key.char)
                        }
                    }
                    else -> key //keep references to enter and delete
                }
            }
        }

        _wordleUiState.value = _wordleUiState.value.copy(
            keyboard = WordleKeyboard(keyboardRows)
        )

    }

    private fun updateCurrentAttempt(newAttempt: WordleAttempt) {
        _wordleUiState.value = _wordleUiState.value.run {
            val newAttempts = attempts.toMutableList().apply {
                set(currentAttemptIndex, newAttempt)
            }

            copy(attempts = newAttempts)
        }
    }

    fun restartAttempts() {
        _wordleUiState.value = generateNewWordleState(numberOfAttempts, lengthOfWord)
        _wordleGameState.value = WordleGameState.Playing
    }

    fun newGame() {
        //TODO generate new word!
        _wordleUiState.value = generateNewWordleState(numberOfAttempts, lengthOfWord)
        _wordleGameState.value = WordleGameState.Playing
    }

    private fun generateNewWordleState(
        numberOfAttempts: Int,
        lengthOfWord: Int
    ) = WordleUiState(WordleAttempt.create(numberOfAttempts, lengthOfWord))

    companion object Factory {
        class Create(
            private val lengthOfWord: Int = 5,
            private val numberOfAttempts: Int = 6
        ) :
            ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                WordleViewModel(lengthOfWord, numberOfAttempts) as T
        }
    }
}