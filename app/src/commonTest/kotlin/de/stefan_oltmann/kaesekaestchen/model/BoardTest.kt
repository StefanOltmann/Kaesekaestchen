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
package de.stefan_oltmann.kaesekaestchen.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for the board construction and the move handling.
 */
class BoardTest {

    @Test
    fun createsExpectedNumberOfBoxes() {

        for (boardSize in BoardSize.values()) {

            val board = Board(boardSize)

            assertEquals(
                boardSize.widthInBoxes * boardSize.heightInBoxes,
                board.boxes.size
            )

            assertTrue(board.hasFreeLines())
            assertTrue(!board.allBoxesHaveOwner())
        }
    }

    @Test
    fun linesAreSharedWithNeighbors() {

        val board = Board(BoardSize.VERY_SMALL)

        val left = board.getBox(0, 0)
        val right = board.getBox(1, 0)

        val top = board.getBox(1, 0)
        val below = board.getBox(1, 1)

        assertNotNull(left.rightLine)
        assertNotNull(right.leftLine)
        assertEquals(left.rightLine, right.leftLine)

        assertNotNull(top.bottomLine)
        assertNotNull(below.topLine)
        assertEquals(top.bottomLine, below.topLine)
    }

    @Test
    fun outerEdgesHaveNoLines() {

        val board = Board(BoardSize.VERY_SMALL)

        val topLeft = board.getBox(0, 0)

        assertNull(topLeft.topLine)
        assertNull(topLeft.leftLine)

        val bottomRight = board.getBox(2, 3)

        assertNull(bottomRight.bottomLine)
        assertNull(bottomRight.rightLine)
    }

    @Test
    fun isInsideGrid() {

        val board = Board(BoardSize.VERY_SMALL)

        assertTrue(board.isInsideGrid(0, 0))
        assertTrue(board.isInsideGrid(2, 3))
        assertTrue(!board.isInsideGrid(3, 3))
        assertTrue(!board.isInsideGrid(0, 4))
        assertTrue(!board.isInsideGrid(-1, 0))
        assertTrue(!board.isInsideGrid(0, -1))
    }

    @Test
    fun getBoxOutsideGridThrows() {

        val board = Board(BoardSize.VERY_SMALL)

        assertFailsWith<IllegalArgumentException> {
            board.getBox(3, 0)
        }

        assertFailsWith<IllegalArgumentException> {
            board.getBox(0, -1)
        }
    }

    @Test
    fun chooseLineSetsOwnerAndLastSetLine() {

        val board = Board(BoardSize.VERY_SMALL)

        val line = board.getBox(0, 0).rightLine!!

        assertNull(line.owner)

        val boxWasClosed = board.chooseLine(line, Player.CHEESE)

        assertTrue(!boxWasClosed)
        assertEquals(Player.CHEESE, line.owner)
        assertEquals(line, board.lastSetLine)
    }

    @Test
    fun closesBoxAndGivesExtraTurn() {

        val board = Board(BoardSize.VERY_SMALL)

        val box = board.getBox(1, 1)

        assertTrue(!board.chooseLine(box.topLine!!, Player.CHEESE))
        assertTrue(!board.chooseLine(box.leftLine!!, Player.CHEESE))
        assertTrue(!board.chooseLine(box.bottomLine!!, Player.CHEESE))

        assertNull(box.owner)

        val boxWasClosed = board.chooseLine(box.rightLine!!, Player.CHEESE)

        assertTrue(boxWasClosed)
        assertEquals(Player.CHEESE, box.owner)
    }

    @Test
    fun countsPoints() {

        val board = Board(BoardSize.VERY_SMALL)

        assertEquals(0, board.countPoints(Player.CHEESE))
        assertEquals(0, board.countPoints(Player.MOUSE))

        closeBox(board, 0, 0, Player.CHEESE)
        closeBox(board, 1, 0, Player.MOUSE)

        assertEquals(1, board.countPoints(Player.CHEESE))
        assertEquals(1, board.countPoints(Player.MOUSE))
    }

    @Test
    fun gameIsOverWhenAllBoxesHaveOwner() {

        val board = Board(BoardSize.VERY_SMALL)

        var player = Player.CHEESE

        for (box in board.boxes) {

            for (line in box.linesWithoutOwner)
                board.chooseLine(line, player)

            if (box.owner == null)
                player = switchPlayer(player)
        }

        assertTrue(board.allBoxesHaveOwner())
        assertTrue(!board.hasFreeLines())

        assertEquals(
            board.boxes.size,
            board.countPoints(Player.CHEESE) + board.countPoints(Player.MOUSE)
        )
    }

    private fun closeBox(
        board: Board,
        gridX: Int,
        gridY: Int,
        player: Player
    ) {

        val box = board.getBox(gridX, gridY)

        for (line in box.linesWithoutOwner)
            board.chooseLine(line, player)
    }

    private fun switchPlayer(player: Player) =
        if (player == Player.CHEESE) Player.MOUSE else Player.CHEESE
}
