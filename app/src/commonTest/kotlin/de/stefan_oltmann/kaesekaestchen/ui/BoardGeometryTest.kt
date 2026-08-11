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

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import de.stefan_oltmann.kaesekaestchen.model.Board
import de.stefan_oltmann.kaesekaestchen.model.BoardSize
import de.stefan_oltmann.kaesekaestchen.model.Player
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tests for the line hit testing on the board.
 */
class BoardGeometryTest {

    private val board = Board(BoardSize.SMALL)

    private val area = Size(420f, 520f)

    private val paddingPx = 24f

    private val geometry = boardGeometry(board, area, paddingPx)

    @Test
    fun tapOnBottomEdgeReturnsBottomLine() {

        val tap = tapAt(boxX = 0, boxY = 0, edgeX = geometry.sideLength / 2, edgeY = geometry.sideLength)

        assertEquals(board.getBox(0, 0).bottomLine, findLineAtPosition(board, tap, geometry))
    }

    @Test
    fun tapOnTopEdgeReturnsTopLine() {

        val tap = tapAt(boxX = 1, boxY = 1, edgeX = geometry.sideLength / 2, edgeY = 0f)

        assertEquals(board.getBox(1, 1).topLine, findLineAtPosition(board, tap, geometry))
    }

    @Test
    fun tapOnLeftEdgeReturnsLeftLine() {

        val tap = tapAt(boxX = 1, boxY = 1, edgeX = 0f, edgeY = geometry.sideLength / 2)

        assertEquals(board.getBox(1, 1).leftLine, findLineAtPosition(board, tap, geometry))
    }

    @Test
    fun tapOnRightEdgeReturnsRightLine() {

        val tap = tapAt(boxX = 1, boxY = 1, edgeX = geometry.sideLength, edgeY = geometry.sideLength / 2)

        assertEquals(board.getBox(1, 1).rightLine, findLineAtPosition(board, tap, geometry))
    }

    @Test
    fun tapInMiddleOfBoxReturnsNull() {

        val tap = tapAt(boxX = 1, boxY = 1, edgeX = geometry.sideLength / 2, edgeY = geometry.sideLength / 2)

        assertNull(findLineAtPosition(board, tap, geometry))
    }

    @Test
    fun tapOutsideBoardReturnsNull() {

        val tap = Offset(-50f, -50f)

        assertNull(findLineAtPosition(board, tap, geometry))
    }

    @Test
    fun tapOnOuterBorderReturnsNull() {

        val tap = tapAt(boxX = 0, boxY = 0, edgeX = 0f, edgeY = geometry.sideLength / 2)

        assertNull(findLineAtPosition(board, tap, geometry))
    }

    @Test
    fun tapOnOwnedBoxReturnsNull() {

        val box = board.getBox(0, 0)

        box.owner = Player.CHEESE

        /* The middle of an owned box is not a line. */
        val tap = tapAt(boxX = 0, boxY = 0, edgeX = geometry.sideLength / 2, edgeY = geometry.sideLength / 2)

        assertNull(findLineAtPosition(board, tap, geometry))
    }

    /*
     * Computes the tap position for a point relative to a box origin.
     */
    private fun tapAt(
        boxX: Int,
        boxY: Int,
        edgeX: Float,
        edgeY: Float
    ): Offset {

        val x = boxX * geometry.sideLength + paddingPx + geometry.offsetX
        val y = boxY * geometry.sideLength + paddingPx + geometry.offsetY

        return Offset(x + edgeX, y + edgeY)
    }
}
