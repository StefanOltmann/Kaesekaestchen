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
import de.stefan_oltmann.kaesekaestchen.model.GameMode
import de.stefan_oltmann.kaesekaestchen.model.Line
import de.stefan_oltmann.kaesekaestchen.model.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * This class is responsible for handling the game flow.
 *
 * Computer moves are executed asynchronously in the given [scope], with
 * delays in between so that the user can follow the actions of the
 * computer.
 *
 * @property board The board the game is played on.
 * @param gameMode The game mode, single player or multi player.
 * @param scope The scope the asynchronous computer moves run in. Cancel it
 *   to abort the game loop.
 * @param random The random source used for the computer opponent and
 *   move selection.
 */
class GameLogic(
    val board: Board,
    gameMode: GameMode,
    private val scope: CoroutineScope,
    private val random: Random = Random.Default
) {

    private lateinit var callback: GameLogicCallback

    private val playerManager = PlayerManager()

    /** The player the computer plays as, or null in multi player mode. */
    val computerOpponent: Player?
        get() = playerManager.computerOpponent

    /** The player whose turn it is right now. */
    val currentPlayer: Player
        get() = playerManager.currentPlayer

    private var moveIsBeingExecuted: Boolean = false

    /**
     * True while the computer move loop is still running, so that the loop
     * is never started a second time. Every player move or box close that
     * leaves the computer at turn would otherwise start another loop, and
     * two loops would double the computer's moves.
     */
    private var computerMoveLoopRunning: Boolean = false

    init {

        if (gameMode == GameMode.SINGLE_PLAYER)
            playerManager.chooseRandomComputerOpponent(random)
    }

    /**
     * Starts the game flow.
     *
     * @param callback The UI callback for the game events.
     */
    fun start(callback: GameLogicCallback) {

        this.callback = callback

        callback.onPlayerTurn(currentPlayer)

        if (playerManager.isComputerOpponent(currentPlayer))
            executeComputerMove()
    }

    /**
     * Handles a player's input for a line.
     *
     * Lines that are already owned are ignored. Input during a computer
     * move is executed for the current player, which is the computer
     * during its own turn.
     *
     * @param line The line the player tapped on.
     */
    fun handlePlayerInput(line: Line) {

        /* Already owned lines cannot be selected. */
        if (line.owner != null)
            return

        /*
         * This check prevents a user, who clicks wildly during the
         * computer's move, from disturbing it.
         */
        if (moveIsBeingExecuted)
            return

        try {

            moveIsBeingExecuted = true

            /*
             * Perform the action for the player
             */
            chooseLineForCurrentPlayerAndCheckGameEnded(line)

            if (playerManager.isComputerOpponent(currentPlayer))
                executeComputerMove()

        } finally {

            moveIsBeingExecuted = false
        }
    }

    private fun executeComputerMove() {

        check(playerManager.isComputerOpponent(currentPlayer)) {
            "Should only be called when the computer is at turn."
        }

        /* A loop is already running; it continues with the computer moves. */
        if (computerMoveLoopRunning)
            return

        /*
         * Execute the computer opponent's reply asynchronously. This
         * happens separately so that the loop can sleep in between and
         * the user can follow the computer's actions while the UI is
         * updated in parallel.
         */
        computerMoveLoopRunning = true

        scope.launch {

            try {

                while (!isGameEnded() && playerManager.isComputerOpponent(currentPlayer)) {

                    /* The player should see the computer's action. */
                    delay(DELAY_BETWEEN_MOVES)

                    /*
                     * Attention: This should not be possible, but from time to
                     * time this problem occurs for some reason. This is only a
                     * workaround for now.
                     */
                    if (!board.hasFreeLines())
                        break

                    val computerLine = board.findGoodLineForComputerMove(random)

                    chooseLineForCurrentPlayerAndCheckGameEnded(computerLine)
                }

            } finally {

                computerMoveLoopRunning = false
            }
        }
    }

    private fun chooseLineForCurrentPlayerAndCheckGameEnded(line: Line) {

        callback.onPlayerTurn(currentPlayer)

        val boxCouldBeClosed =
            board.chooseLine(line, currentPlayer)

        callback.refreshBoardView()

        /*
         * Is it over?
         */
        checkGameEnded()

        /*
         * If a box could be closed, the same player is at turn again.
         * If not, the other player is at turn.
         */
        if (!boxCouldBeClosed)
            playerManager.selectNextPlayer()

        callback.onPlayerTurn(currentPlayer)
    }

    private fun checkGameEnded() {

        if (!isGameEnded())
            return

        scope.launch {

            /*
             * Wait another second, so that the player can calmly look at
             * the final situation.
             */
            delay(DELAY_GAME_ENDED)

            val winner = findPlayerWithHighestScore()

            callback.onGameEnded(
                winner,
                board.countPoints(Player.CHEESE),
                board.countPoints(Player.MOUSE)
            )
        }
    }

    /**
     * The game is over when all boxes have an owner.
     */
    private fun isGameEnded() = board.allBoxesHaveOwner()

    /**
     * The player with the higher score, or null when both scores are equal.
     */
    private fun findPlayerWithHighestScore(): Player? {

        val cheeseScore = board.countPoints(Player.CHEESE)
        val mouseScore = board.countPoints(Player.MOUSE)

        return when {
            cheeseScore > mouseScore -> Player.CHEESE
            mouseScore > cheeseScore -> Player.MOUSE
            else -> null
        }
    }

    companion object {
        private const val DELAY_BETWEEN_MOVES = 500L
        private const val DELAY_GAME_ENDED = 1000L
    }
}
