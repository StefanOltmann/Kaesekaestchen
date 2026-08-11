/*
 * Kaesekaestchen
 * A simple Dots'n'Boxes Game for Android
 *
 * Copyright (C) Stefan Oltmann
 *
 * This file is part of Kaesekaestchen.
 *
 * Kaesekaestchen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kaesekaestchen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kaesekaestchen. If not, see <http://www.gnu.org/licenses/>.
 */
package de.stefan_oltmann.kaesekaestchen.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import de.stefan_oltmann.kaesekaestchen.settings
import de.stefan_oltmann.kaesekaestchen.ui.theme.KaesekaestchenTheme

private const val TRANSITION_DURATION_MS = 300

/**
 * Main Compose entry point for the application UI.
 *
 * The screens slide in from the right, like the original app.
 *
 * @param storage The settings storage; defaults to the platform storage.
 */
@Composable
fun App(
    storage: Settings = settings
) {

    KaesekaestchenTheme {

        var screen by remember { mutableStateOf<Screen>(Screen.Start) }

        AnimatedContent(
            targetState = screen,
            transitionSpec = {

                (slideInHorizontally(tween(TRANSITION_DURATION_MS)) { it } +
                    fadeIn(tween(TRANSITION_DURATION_MS)))
                    .togetherWith(
                        slideOutHorizontally(tween(TRANSITION_DURATION_MS)) { -it } +
                            fadeOut(tween(TRANSITION_DURATION_MS))
                    )
            },
            label = "screen"
        ) { currentScreen ->

            when (currentScreen) {

                Screen.Start -> StartScreen(
                    storage = storage,
                    onPlayClick = { gameMode, boardSize ->
                        screen = Screen.Game(gameMode, boardSize)
                    }
                )

                is Screen.Game -> GameScreen(
                    gameMode = currentScreen.gameMode,
                    boardSize = currentScreen.boardSize,
                    onBackToMenu = { screen = Screen.Start },
                    onGameEnd = { winner, cheeseScore, mouseScore ->
                        screen = Screen.GameOver(winner, cheeseScore, mouseScore)
                    }
                )

                is Screen.GameOver -> ScoreboardScreen(
                    winner = currentScreen.winner,
                    cheeseScore = currentScreen.cheeseScore,
                    mouseScore = currentScreen.mouseScore,
                    onMainMenu = { screen = Screen.Start }
                )
            }
        }
    }
}
