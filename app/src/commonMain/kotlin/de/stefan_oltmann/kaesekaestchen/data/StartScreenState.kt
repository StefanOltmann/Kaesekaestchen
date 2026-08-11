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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import de.stefan_oltmann.kaesekaestchen.model.BoardSize
import de.stefan_oltmann.kaesekaestchen.model.GameMode

/**
 * Holds the start screen settings and persists them through the given
 * [Settings] storage.
 *
 * The keys match the preferences of the original app, so that existing
 * players keep their settings after the update. Values written by the
 * original app use the old German enum names and are mapped to the new
 * names on read.
 */
class StartScreenState(
    private val settings: Settings
) {

    /**
     * The selected game mode, persisted on change.
     */
    var gameMode: GameMode by mutableStateOf(readGameMode())
        private set

    /**
     * The selected board size, persisted on change.
     */
    var boardSize: BoardSize by mutableStateOf(readBoardSize())
        private set

    /**
     * Selects the game mode and persists it.
     */
    fun selectGameMode(gameMode: GameMode) {

        this.gameMode = gameMode

        settings.putString(KEY_GAME_MODE, gameMode.name)
    }

    /**
     * Selects the board size and persists it.
     */
    fun selectBoardSize(boardSize: BoardSize) {

        this.boardSize = boardSize

        settings.putString(KEY_BOARD_SIZE, boardSize.name)
    }

    private fun readGameMode(): GameMode {

        val name = settings.getString(KEY_GAME_MODE, GameMode.SINGLE_PLAYER.name)

        val gameMode = gameModeByName(name) ?: GameMode.SINGLE_PLAYER

        /* Migrate legacy and unknown values to the new names once. */
        if (name != gameMode.name)
            settings.putString(KEY_GAME_MODE, gameMode.name)

        return gameMode
    }

    private fun readBoardSize(): BoardSize {

        val name = settings.getString(KEY_BOARD_SIZE, BoardSize.SMALL.name)

        val boardSize = boardSizeByName(name) ?: BoardSize.SMALL

        /* Migrate legacy and unknown values to the new names once. */
        if (name != boardSize.name)
            settings.putString(KEY_BOARD_SIZE, boardSize.name)

        return boardSize
    }

    private fun gameModeByName(name: String): GameMode? =
        when (name) {
            LEGACY_GAME_MODE_SINGLE_PLAYER, GameMode.SINGLE_PLAYER.name -> GameMode.SINGLE_PLAYER
            LEGACY_GAME_MODE_MULTI_PLAYER, GameMode.MULTI_PLAYER.name -> GameMode.MULTI_PLAYER
            else -> null
        }

    private fun boardSizeByName(name: String): BoardSize? =
        when (name) {
            LEGACY_BOARD_SIZE_VERY_SMALL, BoardSize.VERY_SMALL.name -> BoardSize.VERY_SMALL
            LEGACY_BOARD_SIZE_SMALL, BoardSize.SMALL.name -> BoardSize.SMALL
            LEGACY_BOARD_SIZE_MEDIUM, BoardSize.MEDIUM.name -> BoardSize.MEDIUM
            LEGACY_BOARD_SIZE_LARGE, BoardSize.LARGE.name -> BoardSize.LARGE
            else -> null
        }

    companion object {

        /*
         * The preference file of the original app was called "game_settings".
         * The multiplatform settings backends use a single namespace each,
         * so these keys keep the original names.
         */
        const val KEY_GAME_MODE = "spiel_modus"
        const val KEY_BOARD_SIZE = "feld_groesse"

        /*
         * Values written by the original app before the English rename.
         */
        private const val LEGACY_GAME_MODE_SINGLE_PLAYER = "EINZELSPIELER"
        private const val LEGACY_GAME_MODE_MULTI_PLAYER = "MEHRSPIELER"
        private const val LEGACY_BOARD_SIZE_VERY_SMALL = "SEHR_KLEIN"
        private const val LEGACY_BOARD_SIZE_SMALL = "KLEIN"
        private const val LEGACY_BOARD_SIZE_MEDIUM = "MITTEL"
        private const val LEGACY_BOARD_SIZE_LARGE = "GROSS"
    }
}
