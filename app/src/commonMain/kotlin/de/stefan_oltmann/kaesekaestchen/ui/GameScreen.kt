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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.stefan_oltmann.kaesekaestchen.SystemBackHandler
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.Res
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.app_name
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.back_to_menu
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.current_player
import de.stefan_oltmann.kaesekaestchen.controller.GameLogic
import de.stefan_oltmann.kaesekaestchen.controller.GameLogicCallback
import de.stefan_oltmann.kaesekaestchen.icons.AppIcon
import de.stefan_oltmann.kaesekaestchen.icons.ArrowBack
import de.stefan_oltmann.kaesekaestchen.icons.CheeseSymbol
import de.stefan_oltmann.kaesekaestchen.icons.MouseSymbol
import de.stefan_oltmann.kaesekaestchen.model.Board
import de.stefan_oltmann.kaesekaestchen.model.BoardSize
import de.stefan_oltmann.kaesekaestchen.model.GameMode
import de.stefan_oltmann.kaesekaestchen.model.Player
import de.stefan_oltmann.kaesekaestchen.ui.theme.AppTheme
import de.stefan_oltmann.kaesekaestchen.ui.theme.LocalAppColors
import org.jetbrains.compose.resources.stringResource

/**
 * The game screen: the app name with a back button on top, the current
 * player below, and the board at the bottom.
 *
 * @param gameMode The game mode.
 * @param boardSize The selected board size.
 * @param modifier The modifier applied to the screen.
 * @param initialBoard A board to start the game with, or null for an empty
 *   board of the given size. Used by the screenshot tests to render a
 *   mid-game state.
 * @param onBackToMenu Invoked when the user goes back to the main menu.
 * @param onGameEnd Invoked with the winner (null on a draw) and both
 *   scores once the game is over.
 */
@Composable
fun GameScreen(
    gameMode: GameMode,
    boardSize: BoardSize,
    modifier: Modifier = Modifier,
    initialBoard: Board? = null,
    onBackToMenu: () -> Unit = {},
    onGameEnd: (winner: Player?, cheeseScore: Int, mouseScore: Int) -> Unit = { _, _, _ -> }
) {

    SystemBackHandler(enabled = true, onBack = onBackToMenu)

    val scope = rememberCoroutineScope()

    val gameLogic = remember(gameMode, boardSize, initialBoard) {
        GameLogic(initialBoard ?: Board(boardSize), gameMode, scope)
    }

    var currentPlayer by remember { mutableStateOf(Player.CHEESE) }
    var boardVersion by remember { mutableIntStateOf(0) }

    val callback = remember(gameLogic) {

        object : GameLogicCallback {

            override fun onPlayerTurn(player: Player) {
                currentPlayer = player
            }

            override fun onGameEnded(
                winner: Player?,
                cheeseScore: Int,
                mouseScore: Int
            ) {
                onGameEnd(winner, cheeseScore, mouseScore)
            }

            override fun refreshBoardView() {
                boardVersion++
            }
        }
    }

    LaunchedEffect(gameLogic) {
        gameLogic.start(callback)
    }

    val colors = LocalAppColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {

        /*
         * The top row: the back button and the app name, on a distinct
         * background band.
         */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(horizontal = AppTheme.borderMargin, vertical = 12.dp)
        ) {

            Text(
                text = stringResource(Res.string.app_name),
                fontSize = AppTheme.titleTextSize,
                modifier = Modifier.align(Alignment.Center)
            )

            Icon(
                imageVector = AppIcon.ArrowBack,
                contentDescription = stringResource(Res.string.back_to_menu),
                tint = colors.onSurface,
                modifier = Modifier
                    .size(AppTheme.backButtonSize)
                    .align(Alignment.CenterStart)
                    .clickable(onClick = onBackToMenu)
            )
        }

        /*
         * The second row: the player whose turn it is, centered.
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = AppTheme.borderMargin, top = 16.dp, end = AppTheme.borderMargin),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(Res.string.current_player),
                fontSize = AppTheme.textSize
            )

            Spacer(Modifier.width(8.dp))

            Icon(
                imageVector = if (currentPlayer == Player.CHEESE)
                    AppIcon.CheeseSymbol
                else
                    AppIcon.MouseSymbol,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(AppTheme.currentPlayerSymbolSize)
            )
        }

        BoardView(
            board = gameLogic.board,
            boardVersion = boardVersion,
            onLineSelect = { gameLogic.handlePlayerInput(it) },
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = AppTheme.borderMargin,
                    top = 16.dp,
                    end = AppTheme.borderMargin,
                    bottom = AppTheme.borderMargin
                )
        )

        AppFooter()
    }
}
