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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tests for the [PlayerManager].
 */
class PlayerManagerTest {

    @Test
    fun startsWithCheese() {

        val playerManager = PlayerManager()

        assertEquals(Player.CHEESE, playerManager.currentPlayer)
        assertNull(playerManager.computerOpponent)
    }

    @Test
    fun alternatesPlayers() {

        val playerManager = PlayerManager()

        playerManager.selectNextPlayer()

        assertEquals(Player.MOUSE, playerManager.currentPlayer)

        playerManager.selectNextPlayer()

        assertEquals(Player.CHEESE, playerManager.currentPlayer)
    }

    @Test
    fun choosesRandomComputerOpponentDeterministically() {

        val managerA = PlayerManager()
        val managerB = PlayerManager()

        managerA.chooseRandomComputerOpponent(Random(7))
        managerB.chooseRandomComputerOpponent(Random(7))

        /* Same seed, same opponent */
        assertEquals(managerA.computerOpponent, managerB.computerOpponent)
    }

    @Test
    fun computerOpponentIsExactlyOnePlayer() {

        for (seed in 0L..20L) {

            val playerManager = PlayerManager()

            playerManager.chooseRandomComputerOpponent(Random(seed))

            assertEquals(
                1,
                Player.values().count { playerManager.isComputerOpponent(it) }
            )
        }
    }

    @Test
    fun computerOpponentChangesWithSeed() {

        val opponents = mutableSetOf<Player>()

        for (seed in 0L..20L) {

            val playerManager = PlayerManager()

            playerManager.chooseRandomComputerOpponent(Random(seed))

            playerManager.computerOpponent?.let { opponents.add(it) }
        }

        /* Both players can be the computer opponent */
        assertEquals(Player.values().toSet(), opponents)
    }
}
