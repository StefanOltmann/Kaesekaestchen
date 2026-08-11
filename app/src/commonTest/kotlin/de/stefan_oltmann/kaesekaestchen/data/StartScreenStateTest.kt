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
package de.stefan_oltmann.kaesekaestchen.data

import com.russhwolf.settings.MapSettings
import de.stefan_oltmann.kaesekaestchen.model.BoardSize
import de.stefan_oltmann.kaesekaestchen.model.GameMode
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for the persisted start screen settings.
 */
class StartScreenStateTest {

    @Test
    fun providesDefaultValues() {

        val startScreenState = StartScreenState(MapSettings())

        assertEquals(GameMode.SINGLE_PLAYER, startScreenState.gameMode)
        assertEquals(BoardSize.SMALL, startScreenState.boardSize)
    }

    @Test
    fun storesWithOriginalKeys() {

        val settings = MapSettings()
        val startScreenState = StartScreenState(settings)

        startScreenState.selectGameMode(GameMode.MULTI_PLAYER)
        startScreenState.selectBoardSize(BoardSize.LARGE)

        assertEquals(
            GameMode.MULTI_PLAYER.name,
            settings.getString(StartScreenState.KEY_GAME_MODE, "")
        )
        assertEquals(
            BoardSize.LARGE.name,
            settings.getString(StartScreenState.KEY_BOARD_SIZE, "")
        )
    }

    @Test
    fun restoresStoredValues() {

        val settings = MapSettings()
        val startScreenState = StartScreenState(settings)

        startScreenState.selectGameMode(GameMode.MULTI_PLAYER)
        startScreenState.selectBoardSize(BoardSize.MEDIUM)

        val restoredState = StartScreenState(settings)

        assertEquals(GameMode.MULTI_PLAYER, restoredState.gameMode)
        assertEquals(BoardSize.MEDIUM, restoredState.boardSize)
    }

    @Test
    fun acceptsOldOriginalValues() {

        /*
         * Values saved by the original Android app keep working, because
         * the keys and the old German value names are mapped.
         */
        val settings = MapSettings(
            mutableMapOf(
                StartScreenState.KEY_GAME_MODE to "MEHRSPIELER",
                StartScreenState.KEY_BOARD_SIZE to "SEHR_KLEIN"
            )
        )

        val startScreenState = StartScreenState(settings)

        assertEquals(GameMode.MULTI_PLAYER, startScreenState.gameMode)
        assertEquals(BoardSize.VERY_SMALL, startScreenState.boardSize)
    }

    @Test
    fun migratesOldOriginalValuesToNewNames() {

        val settings = MapSettings(
            mutableMapOf(
                StartScreenState.KEY_GAME_MODE to "EINZELSPIELER",
                StartScreenState.KEY_BOARD_SIZE to "GROSS"
            )
        )

        StartScreenState(settings)

        assertEquals(
            GameMode.SINGLE_PLAYER.name,
            settings.getString(StartScreenState.KEY_GAME_MODE, "")
        )
        assertEquals(
            BoardSize.LARGE.name,
            settings.getString(StartScreenState.KEY_BOARD_SIZE, "")
        )
    }

    @Test
    fun unknownValuesFallBackToDefaults() {

        val settings = MapSettings(
            mutableMapOf(
                StartScreenState.KEY_GAME_MODE to "UNBEKANNT",
                StartScreenState.KEY_BOARD_SIZE to "RIESIG"
            )
        )

        val startScreenState = StartScreenState(settings)

        assertEquals(GameMode.SINGLE_PLAYER, startScreenState.gameMode)
        assertEquals(BoardSize.SMALL, startScreenState.boardSize)
    }
}
