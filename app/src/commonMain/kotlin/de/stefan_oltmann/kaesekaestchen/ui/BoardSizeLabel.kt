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

import androidx.compose.runtime.Composable
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.Res
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.board_size_large
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.board_size_medium
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.board_size_small
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.board_size_very_small
import de.stefan_oltmann.kaesekaestchen.model.BoardSize
import org.jetbrains.compose.resources.stringResource

/**
 * The localized name of the given board size.
 *
 * @param boardSize The board size.
 */
@Composable
internal fun boardSizeLabel(boardSize: BoardSize): String =
    when (boardSize) {

        BoardSize.VERY_SMALL -> stringResource(Res.string.board_size_very_small)
        BoardSize.SMALL -> stringResource(Res.string.board_size_small)
        BoardSize.MEDIUM -> stringResource(Res.string.board_size_medium)
        BoardSize.LARGE -> stringResource(Res.string.board_size_large)
    }
