package com.erm.composegames.ui.wordle

import androidx.compose.ui.graphics.Color
import com.erm.composegames.ui.theme.*

data class WordleUiState(
    val attempts: List<WordleAttempt>,
    val keyboard: WordleKeyboard = WordleKeyboard.default()
)

data class WordleAttempt(val letters: List<WordleKey.WordleLetter>) {
    companion object {
        fun create(attempts: Int, lengthOfWord: Int): List<WordleAttempt> = (0 until attempts).map {
            WordleAttempt(List<WordleKey.WordleLetter>(lengthOfWord) {
                WordleKey.WordleLetter.Pending()
            })
        }
    }
}

data class WordleKeyboard(val keys: List<List<WordleKey>>) {
    companion object {
        fun default(): WordleKeyboard = WordleKeyboard(
            listOf(
                "qwertyuiop".toWordleLetters(),
                "asdfghjkl".toWordleLetters(),
                listOf(WordleKey.WordleFunctionKey.Enter) + "zxcvbnm".toWordleLetters() + listOf(
                    WordleKey.WordleFunctionKey.Delete)
            )
        )
    }
}

sealed class WordleKey(val text: String?) {
    sealed class WordleFunctionKey(text: String) : WordleKey(text) {
        object Enter : WordleFunctionKey("Enter")
        object Delete : WordleFunctionKey("Delete")
    }

    sealed class WordleLetter(open val char: Char?, val color: Color) : WordleKey(char.toString()) {
        data class Correct(override val char: Char) : WordleLetter(char, Correct)
        data class WrongPosition(override val char: Char) : WordleLetter(char, WrongPosition)
        data class Absent(override val char: Char) : WordleLetter(char, Absent)
        data class InvalidWord(override val char: Char) : WordleLetter(char, InvalidWord)
        data class Pending(override val char: Char? = null) : WordleLetter(char, Pending)

        companion object {
            fun WordleLetter.isEnteredValidWord() = this !is InvalidWord && this !is Pending
        }
    }
}


fun String.toWordleLetters() = map { WordleKey.WordleLetter.Pending(it) }
fun Char.toPending() = WordleKey.WordleLetter.Pending(this)