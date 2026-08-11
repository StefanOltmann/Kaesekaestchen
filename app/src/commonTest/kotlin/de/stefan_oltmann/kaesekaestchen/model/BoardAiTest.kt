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

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for the computer move selection on the [Board].
 */
class BoardAiTest {

    @Test
    fun alwaysClosesPossibleBox() {

        val board = Board(BoardSize.VERY_SMALL)

        val box = board.getBox(1, 1)

        board.chooseLine(box.topLine!!, Player.CHEESE)
        board.chooseLine(box.leftLine!!, Player.CHEESE)
        board.chooseLine(box.bottomLine!!, Player.CHEESE)

        assertNull(box.owner)

        val computerMove = board.findGoodLineForComputerMove(Random(0))

        assertEquals(box.rightLine, computerMove)

        board.chooseLine(computerMove, Player.MOUSE)

        assertEquals(Player.MOUSE, box.owner)
    }

    @Test
    fun avoidsGivingAwayBoxesWhenPossible() {

        val board = Board(BoardSize.VERY_SMALL)

        /*
         * Give the interior box (1,1) two lines, so that its two remaining
         * lines would leave it completable by the opponent. All other lines
         * are safe.
         */
        val box = board.getBox(1, 1)

        board.chooseLine(box.topLine!!, Player.CHEESE)
        board.chooseLine(box.leftLine!!, Player.CHEESE)

        val dangerousLines = listOf(box.bottomLine, box.rightLine)

        val computerMove = board.findGoodLineForComputerMove(Random(1))

        assertTrue(
            computerMove !in dangerousLines,
            "The computer gave away a box: $computerMove"
        )
        assertNull(computerMove.owner)
    }

    @Test
    fun givesUpWhenOnlyDangerousLinesRemain() {

        val board = Board(BoardSize.VERY_SMALL)

        /*
         * Fill every line except the two remaining lines of the interior
         * box (1,1), so that only dangerous lines are left.
         */
        val box = board.getBox(1, 1)

        val openLines = allFreeLines(board)

        for (line in openLines) {

            if (line != box.bottomLine && line != box.rightLine)
                board.chooseLine(line, Player.CHEESE)
        }

        val computerMove = board.findGoodLineForComputerMove(Random(2))

        assertTrue(computerMove == box.bottomLine || computerMove == box.rightLine)
        assertNull(computerMove.owner)
    }

    @Test
    fun neverChoosesOwnedLine() {

        val board = Board(BoardSize.VERY_SMALL)

        board.chooseLine(board.getBox(0, 0).rightLine!!, Player.CHEESE)

        for (seed in 0L..19L) {

            val computerMove = board.findGoodLineForComputerMove(Random(seed))

            assertNull(computerMove.owner)
        }
    }

    @Test
    fun playsWholeGameWithoutErrors() {

        val board = Board(BoardSize.MEDIUM)

        var player = Player.CHEESE

        while (!board.allBoxesHaveOwner()) {

            val line = board.findGoodLineForComputerMove(Random(3))

            assertNotNull(line)
            assertNull(line.owner)

            val boxWasClosed = board.chooseLine(line, player)

            if (!boxWasClosed)
                player = if (player == Player.CHEESE) Player.MOUSE else Player.CHEESE
        }

        assertEquals(
            board.boxes.size,
            board.countPoints(Player.CHEESE) + board.countPoints(Player.MOUSE)
        )
    }

    private fun allFreeLines(board: Board): List<Line> {

        val lines = mutableSetOf<Line>()

        for (box in board.boxes)
            lines.addAll(box.linesWithoutOwner)

        return lines.toList()
    }
}
