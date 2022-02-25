package com.erm.composegames.ui.snake

enum class SnakeMoveResult {
    OK, //Moved in a valid position
    ATE, //Ate a fruit
    OB, //Moved out of bounds
    CLASH //Clashed with a part of the snak
}