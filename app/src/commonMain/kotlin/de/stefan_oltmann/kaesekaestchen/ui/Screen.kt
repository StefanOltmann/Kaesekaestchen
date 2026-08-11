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

import de.stefan_oltmann.kaesekaestchen.model.BoardSize
import de.stefan_oltmann.kaesekaestchen.model.GameMode
import de.stefan_oltmann.kaesekaestchen.model.Player

/**
 * The screens of the app, mirroring the navigation graph of the original app.
 */
sealed interface Screen {

    /**
     * The main menu.
     */
    data object Start : Screen

    /**
     * The running game.
     */
    data class Game(
        val gameMode: GameMode,
        val boardSize: BoardSize
    ) : Screen

    /**
     * The scoreboard shown at the end of the game with the trophy of the
     * winner, both scores and a button back to the main menu. The winner
     * is null when the game ended in a draw.
     */
    data class GameOver(
        val winner: Player?,
        val cheeseScore: Int,
        val mouseScore: Int
    ) : Screen
}
