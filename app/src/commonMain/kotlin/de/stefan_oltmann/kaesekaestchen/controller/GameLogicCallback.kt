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
package de.stefan_oltmann.kaesekaestchen.controller

import de.stefan_oltmann.kaesekaestchen.model.Player

/**
 * Implemented by the UI layer so that the [GameLogic] is not tied to
 * any specific UI framework.
 */
interface GameLogicCallback {

    /**
     * Called when a player's turn begins.
     *
     * @param player The player whose turn it is.
     */
    fun onPlayerTurn(player: Player)

    /**
     * Called once the game is over.
     *
     * @param winner The player with the higher score, or null when the
     *   game ends in a draw.
     * @param cheeseScore The boxes owned by the cheese player.
     * @param mouseScore The boxes owned by the mouse player.
     */
    fun onGameEnded(winner: Player?, cheeseScore: Int, mouseScore: Int)

    /**
     * Called after every move so that the board can be redrawn.
     */
    fun refreshBoardView()
}
