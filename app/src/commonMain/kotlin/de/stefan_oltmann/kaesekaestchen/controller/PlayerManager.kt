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
import kotlin.random.Random

/**
 * The [PlayerManager] determines which player is at turn and selects the
 * next player.
 */
class PlayerManager {

    /** The player whose turn it is right now. */
    private var _currentPlayer: Player = Player.CHEESE

    val currentPlayer: Player
        get() = _currentPlayer

    /** The player the computer plays as, or null in multi player mode. */
    private var _computerOpponent: Player? = null

    val computerOpponent: Player?
        get() = _computerOpponent

    /**
     * The computer opponent is determined randomly, so that both the symbol
     * and the starting player are varied between games.
     */
    fun chooseRandomComputerOpponent(random: Random) {
        _computerOpponent = Player.values()[random.nextInt(Player.values().size)]
    }

    /**
     * True when the computer plays as the given player.
     */
    fun isComputerOpponent(player: Player) =
        player == _computerOpponent

    /**
     * Selects the other player as the next player.
     */
    fun selectNextPlayer() {

        _currentPlayer =
            if (_currentPlayer == Player.CHEESE)
                Player.MOUSE
            else
                Player.CHEESE
    }
}
