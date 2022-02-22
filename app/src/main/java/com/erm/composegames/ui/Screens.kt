package com.erm.composegames.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screens(
    val icon: ImageVector,
    val isGame: Boolean = true
) {
    Home(
        icon = Icons.Filled.Home,
        isGame = false
    ),
    Snake(
        icon = Icons.Filled.PlayArrow
    ),
    Wordle(
        icon = Icons.Filled.PlayArrow
    );

    companion object {
        val games = values().filter { it.isGame }
    }
}
