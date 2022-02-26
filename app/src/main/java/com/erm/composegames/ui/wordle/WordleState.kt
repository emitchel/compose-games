package com.erm.composegames.ui.wordle

data class WordleState(
    val attempts: List<WordleAttempt>,
    val keyboard: WordleKeyboard = WordleKeyboard.default()
)

data class WordleAttempt(val letters: List<WordleKey.WordleLetter>) {
    companion object {
        fun create(attempts: Int, lengthOfWord: Int): List<WordleAttempt> = (0 until attempts).map {
            WordleAttempt(List<WordleKey.WordleLetter>(lengthOfWord) {
                WordleKey.WordleLetter.Pending(null)
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
                listOf(WordleKey.Enter) + "zxcvbnm".toWordleLetters() + listOf(WordleKey.Delete)
            )
        )
    }
}

sealed class WordleKey {
    object Enter : WordleKey()
    object Delete : WordleKey()
    sealed class WordleLetter(open val char: Char?) : WordleKey() {
        data class Green(override val char: Char) : WordleLetter(char)
        data class Yellow(override val char: Char) : WordleLetter(char)
        data class Gray(override val char: Char) : WordleLetter(char)
        data class Invalid(override val char: Char) : WordleLetter(char)
        data class Pending(override val char: Char?) : WordleLetter(char)
    }
}


fun String.toWordleLetters() = map { WordleKey.WordleLetter.Pending(it) }
fun Char.toPending() = WordleKey.WordleLetter.Pending(this)