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
import de.stefan_oltmann.kaesekaestchen.model.Board

/**
 * The owner symbols, placed inside their boxes.
 *
 * @param board The board to render.
 * @param geometry The computed board geometry.
 * @param symbolPaddingPx The padding around each symbol.
 */
@Composable
internal fun OwnerSymbols(
    board: Board,
    geometry: BoardGeometry,
    symbolPaddingPx: Float
) {

    for (box in board.boxes)
        OwnerSymbol(
            box = box,
            geometry = geometry,
            symbolPaddingPx = symbolPaddingPx
        )
}
