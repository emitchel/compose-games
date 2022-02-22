package com.erm.composegames

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erm.composegames.ui.Screens
import com.erm.composegames.ui.home.HomeScreen
import com.erm.composegames.ui.snake.SnakeScreen
import com.erm.composegames.ui.theme.ComposeGamesTheme
import com.erm.composegames.ui.wordle.WordleScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }

    @Composable
    private fun App() {
        ComposeGamesTheme {
            val navController = rememberNavController()
            Scaffold(
                //TODO settings func
                //                    topBar = {
                //                        RallyTabRow(
                //                            allScreens = allScreens,
                //                            onTabSelected = { screen -> currentScreen = screen },
                //                            currentScreen = currentScreen
                //                        )
                //                    }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screens.Home.name,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screens.Home.name) {
                        HomeScreen {
                            navController.navigate(it.name)
                        }
                    }
                    composable(Screens.Snake.name) {
                        SnakeScreen()
                    }
                    composable(Screens.Wordle.name) {
                        WordleScreen()
                    }
                }
            }
        }
    }
}