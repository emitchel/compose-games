package com.erm.composegames.ui.wordle

data class WordleState(
    val attempts: List<WordleAttempt>,
    val keyboard: WordleKeyboard = WordleKeyboard.default()
)

data class WordleAttempt(val letters: List<WordleKeys.WordleLetter>) {
    companion object {
        fun create(attempts: Int, lengthOfWord: Int): List<WordleAttempt> = (0 until attempts).map {
            WordleAttempt(List<WordleKeys.WordleLetter>(lengthOfWord) {
                WordleKeys.WordleLetter.Pending(null)
            })
        }
    }
}

data class WordleKeyboard(val keys: List<List<WordleKeys>>) {
    companion object {
        fun default(): WordleKeyboard = WordleKeyboard(
            listOf(
                "qwertyuiop".toWordleLetters(),
                "asdfghjkl".toWordleLetters(),
                listOf(WordleKeys.Enter) + "zxcvbnm".toWordleLetters() + listOf(WordleKeys.Delete)
            )
        )
    }
}

sealed class WordleKeys {
    object Enter : WordleKeys()
    object Delete : WordleKeys()
    sealed class WordleLetter(open val char: Char?) : WordleKeys() {
        data class Green(override val char: Char) : WordleLetter(char)
        data class Yellow(override val char: Char) : WordleLetter(char)
        data class Gray(override val char: Char) : WordleLetter(char)
        data class Invalid(override val char: Char) : WordleLetter(char)
        data class Pending(override val char: Char?) : WordleLetter(char)
    }
}


fun String.toWordleLetters() = map { WordleKeys.WordleLetter.Pending(it) }
fun Char.toPending() = WordleKeys.WordleLetter.Pending(this)