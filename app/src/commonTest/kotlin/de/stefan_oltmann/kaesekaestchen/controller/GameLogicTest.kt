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

import de.stefan_oltmann.kaesekaestchen.model.Board
import de.stefan_oltmann.kaesekaestchen.model.BoardSize
import de.stefan_oltmann.kaesekaestchen.model.GameMode
import de.stefan_oltmann.kaesekaestchen.model.Line
import de.stefan_oltmann.kaesekaestchen.model.Player
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

/**
 * Tests for the [GameLogic] game flow with virtual time.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameLogicTest {

    @Test
    fun multiPlayerStartsWithCheese() = runTest {

        val board = Board(BoardSize.VERY_SMALL)
        val gameLogic = GameLogic(board, GameMode.MULTI_PLAYER, backgroundScope)
        val callback = RecordingCallback()

        gameLogic.start(callback)

        assertEquals(Player.CHEESE, callback.lastPlayerTurn)
    }

    @Test
    fun multiPlayerSwitchesAfterMoveWithoutBox() = runTest {

        val board = Board(BoardSize.VERY_SMALL)
        val gameLogic = GameLogic(board, GameMode.MULTI_PLAYER, backgroundScope)
        val callback = RecordingCallback()

        gameLogic.start(callback)

        val line = board.getBox(0, 0).rightLine!!

        gameLogic.handlePlayerInput(line)

        assertEquals(Player.CHEESE, line.owner)
        assertEquals(Player.MOUSE, callback.lastPlayerTurn)
    }

    @Test
    fun multiPlayerStaysAtTurnWhenBoxClosed() = runTest {

        val board = Board(BoardSize.VERY_SMALL)
        val gameLogic = GameLogic(board, GameMode.MULTI_PLAYER, backgroundScope)
        val callback = RecordingCallback()

        gameLogic.start(callback)

        val box = board.getBox(1, 1)

        gameLogic.handlePlayerInput(box.topLine!!)
        gameLogic.handlePlayerInput(box.leftLine!!)
        gameLogic.handlePlayerInput(box.bottomLine!!)

        assertEquals(Player.MOUSE, callback.lastPlayerTurn)

        gameLogic.handlePlayerInput(box.rightLine!!)

        assertEquals(Player.MOUSE, box.owner)

        /* Closing a box keeps the player at turn. */
        assertEquals(Player.MOUSE, callback.lastPlayerTurn)
    }

    @Test
    fun ownedLineIsIgnored() = runTest {

        val board = Board(BoardSize.VERY_SMALL)
        val gameLogic = GameLogic(board, GameMode.MULTI_PLAYER, backgroundScope)
        val callback = RecordingCallback()

        gameLogic.start(callback)

        val line = board.getBox(0, 0).rightLine!!

        gameLogic.handlePlayerInput(line)
        gameLogic.handlePlayerInput(line)

        assertEquals(Player.CHEESE, line.owner)
        assertEquals(Player.MOUSE, callback.lastPlayerTurn)
    }

    @Test
    fun singlePlayerExecutesComputerMoveAfterDelay() = runTest {

        val board = Board(BoardSize.VERY_SMALL)
        val gameLogic = GameLogic(board, GameMode.SINGLE_PLAYER, backgroundScope, Random(0))
        val callback = RecordingCallback()

        gameLogic.start(callback)

        /* No move has happened yet, the delay is pending. */
        assertEquals(0, countOfOwnedLines(board))

        advanceTimeBy(DELAY_BETWEEN_MOVES)

        runCurrent()

        if (gameLogic.computerOpponent == Player.CHEESE) {

            /* The computer started and made its first move. */
            assertEquals(1, countOfOwnedLines(board))
            assertNotNull(board.lastSetLine)

        } else {

            /* The human started, so nothing has happened yet. */
            assertEquals(0, countOfOwnedLines(board))

            gameLogic.handlePlayerInput(board.getBox(0, 0).rightLine!!)

            /* Now the computer responds after its delay. */
            advanceTimeBy(DELAY_BETWEEN_MOVES)

            runCurrent()

            assertEquals(2, countOfOwnedLines(board))
        }
    }

    @Test
    fun singlePlayerComputerExecutesMultipleMoves() = runTest {

        val board = Board(BoardSize.SMALL)
        val gameLogic = GameLogic(board, GameMode.SINGLE_PLAYER, backgroundScope, Random(0))
        val callback = RecordingCallback()

        gameLogic.start(callback)

        /*
         * Let the computer play several moves in a row, as long as it
         * stays at turn.
         */
        for (i in 1..10) {

            val linesBefore = countOfOwnedLines(board)

            advanceTimeBy(DELAY_BETWEEN_MOVES)

            runCurrent()

            val linesAfter = countOfOwnedLines(board)

            if (gameLogic.computerOpponent != gameLogic.currentPlayer) {
                /* The human is at turn now, the loop stopped. */
                assertEquals(linesBefore, linesAfter)
                break
            } else {
                assertEquals(linesBefore + 1, linesAfter)
            }
        }
    }

    @Test
    fun playerInputDuringComputerThinking() = runTest {

        val board = Board(BoardSize.VERY_SMALL)
        val gameLogic = GameLogic(board, GameMode.SINGLE_PLAYER, backgroundScope, Random(0))
        val callback = RecordingCallback()

        gameLogic.start(callback)

        val line = board.getBox(0, 0).rightLine!!

        /*
         * While the computer is thinking (its delay is pending), the input
         * is executed for the current player. If the computer starts, this
         * is the computer itself; otherwise it is the human player.
         */
        gameLogic.handlePlayerInput(line)

        assertEquals(Player.CHEESE, line.owner)

        advanceTimeBy(DELAY_BETWEEN_MOVES)

        runCurrent()

        if (gameLogic.computerOpponent == Player.CHEESE) {

            /*
             * The click was the computer's move, now the human is at turn
             * and the pending loop stops.
             */
            assertEquals(1, countOfOwnedLines(board))

        } else {

            /*
             * The click was the human's move, now the computer responds.
             */
            assertEquals(2, countOfOwnedLines(board))
        }
    }

    /*
     * Regression guard for a double computer-move loop: when a box is
     * closed while the computer's move loop is suspended in its delay, the
     * same player stays at turn and the click starts the loop again. Only
     * one computer move may be executed per delay, even then.
     */
    @Test
    fun closingBoxDuringComputerDelayRunsOneMovePerDelay() = runTest {

        val board = Board(BoardSize.VERY_SMALL)
        val gameLogic = GameLogic(board, GameMode.SINGLE_PLAYER, backgroundScope, Random(0))
        val callback = RecordingCallback()

        gameLogic.start(callback)

        /* True when the risky scenario was actually reached. */
        var exercisedRiskyScenario = false

        for (i in 0 until 500) {

            if (board.allBoxesHaveOwner())
                break

            if (advanceToNextTurnAndStopIfOver(board))
                break

            if (exerciseOrLetHumanPlay(board, gameLogic)) {

                exercisedRiskyScenario = true

                break
            }
        }

        assertTrue(
            exercisedRiskyScenario,
            "The search never reached the risky scenario."
        )
    }

    /*
     * Advances one computer delay and reports whether the game ended while
     * waiting for the computer's reply.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun TestScope.advanceToNextTurnAndStopIfOver(
        board: Board
    ): Boolean {

        advanceTimeBy(DELAY_BETWEEN_MOVES)

        runCurrent()

        return board.allBoxesHaveOwner()
    }

    /*
     * Attempts the risky click on the computer's turn, or makes a move for
     * the human player otherwise. Returns true when the risky scenario was
     * exercised and verified.
     */
    private suspend fun TestScope.exerciseOrLetHumanPlay(
        board: Board,
        gameLogic: GameLogic
    ): Boolean {

        if (gameLogic.currentPlayer != gameLogic.computerOpponent) {

            /* Make a move for the human player to keep the game going. */
            gameLogic.handlePlayerInput(firstFreeLine(board))

            return false
        }

        return tryRiskyClickPasses(board, gameLogic)
    }

    /*
     * Replays the risky click: while the computer's move loop is suspended
     * in its delay, a line that closes a box is clicked, which leaves the
     * computer at turn. The click then starts the loop a second time, and
     * this verifies that only one computer move is made per delay. Returns
     * true when the scenario was exercised and verified.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun TestScope.tryRiskyClickPasses(
        board: Board,
        gameLogic: GameLogic
    ): Boolean {

        val closingLine = firstLineClosingABox(board)

        if (closingLine == null || countOpenBoxes(board) <= 1)
            return false

        val ownedLinesBeforeClick = countOfOwnedLines(board)

        gameLogic.handlePlayerInput(closingLine)

        assertFalse(board.allBoxesHaveOwner())

        /* The click itself places exactly one line. */
        val ownedLinesAfterClick = countOfOwnedLines(board)

        assertEquals(ownedLinesBeforeClick + 1, ownedLinesAfterClick)

        advanceTimeBy(DELAY_BETWEEN_MOVES)

        runCurrent()

        assertEquals(
            ownedLinesAfterClick + 1,
            countOfOwnedLines(board),
            "The pending and the re-started computer loop must not both move in one delay."
        )

        return true
    }

    @Test
    fun gameEndedReportsWinnerAndScores() = runTest {

        val board = Board(BoardSize.VERY_SMALL)
        val gameLogic = GameLogic(board, GameMode.MULTI_PLAYER, backgroundScope)
        val callback = RecordingCallback()

        gameLogic.start(callback)

        playGameToEnd(board, gameLogic)

        assertTrue(board.allBoxesHaveOwner())

        assertEquals(0, callback.gameEndedCalls)

        advanceTimeBy(DELAY_GAME_ENDED)

        runCurrent()

        assertEquals(1, callback.gameEndedCalls)
        assertNotNull(callback.winner)
        assertNotNull(callback.cheeseScore)
        assertNotNull(callback.mouseScore)

        assertEquals(
            board.boxes.size,
            callback.cheeseScore!! + callback.mouseScore!!
        )

        val winnerScore =
            if (callback.winner == Player.CHEESE)
                callback.cheeseScore
            else
                callback.mouseScore

        assertEquals(
            maxOf(callback.cheeseScore!!, callback.mouseScore!!),
            winnerScore
        )
    }

    /*
     * The different players both end with six boxes, so the game ends in a
     * draw and no player may be reported as the winner.
     */
    @Test
    fun tiedGameReportsNoWinner() = runTest {

        val board = Board(BoardSize.VERY_SMALL)
        val gameLogic = GameLogic(board, GameMode.MULTI_PLAYER, backgroundScope)
        val callback = RecordingCallback()

        gameLogic.start(callback)

        /* This fixed play order fills the board with six boxes each. */
        for (line in freeLines(board).shuffled(Random(26)))
            gameLogic.handlePlayerInput(line)

        assertTrue(board.allBoxesHaveOwner())

        assertEquals(0, callback.gameEndedCalls)

        advanceTimeBy(DELAY_GAME_ENDED)

        runCurrent()

        assertEquals(1, callback.gameEndedCalls)
        assertNotNull(callback.cheeseScore)
        assertNotNull(callback.mouseScore)

        assertEquals(callback.cheeseScore!!, callback.mouseScore!!)
        assertNull(callback.winner)
    }

    private fun freeLines(board: Board): List<Line> {

        val lines = mutableSetOf<Line>()

        for (box in board.boxes)
            lines.addAll(box.linesWithoutOwner)

        return lines.toList()
    }

    private fun firstLineClosingABox(board: Board): Line? {

        for (box in board.boxes)
            if (box.owner == null && box.linesWithoutOwner.size == 1)
                return box.linesWithoutOwner[0]

        return null
    }

    private fun countOpenBoxes(board: Board): Int =
        board.boxes.count { it.owner == null }

    private fun playGameToEnd(
        board: Board,
        gameLogic: GameLogic
    ) {

        while (!board.allBoxesHaveOwner()) {

            val line = firstFreeLine(board)

            gameLogic.handlePlayerInput(line)
        }
    }

    private fun firstFreeLine(board: Board): Line {

        for (box in board.boxes) {

            val firstLine = box.linesWithoutOwner.firstOrNull()

            if (firstLine != null)
                return firstLine
        }

        throw IllegalStateException("There are no free lines left.")
    }

    private fun countOfOwnedLines(board: Board): Int {

        val ownedLines = mutableSetOf<Line>()

        for (box in board.boxes) {

            for (line in listOf(box.topLine, box.bottomLine, box.leftLine, box.rightLine)) {

                if (line?.owner != null)
                    ownedLines.add(line)
            }
        }

        return ownedLines.size
    }

    companion object {
        private const val DELAY_BETWEEN_MOVES = 500L
        private const val DELAY_GAME_ENDED = 1000L
    }
}
