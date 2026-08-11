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

/**
 * The board, consisting of [Box]es which share their [Line]s with their
 * neighbors.
 */
class Board(
    private val boardSize: BoardSize
) {

    val widthInBoxes
        get() = boardSize.widthInBoxes

    val heightInBoxes
        get() = boardSize.heightInBoxes

    private var boxesArray: Array<Array<Box?>>

    /* The list of all boxes */
    private val _boxesList: MutableList<Box> = mutableListOf()

    val boxes: List<Box>
        get() = _boxesList

    /**
     * A second list is kept for performance, so that the 'boxes' list
     * does not have to be iterated as often. Iterating it frequently
     * would be slow on large boards.
     */
    private val openBoxes: MutableList<Box> = mutableListOf()

    private val linesWithoutOwner: MutableSet<Line> = mutableSetOf()

    /**
     * The most recently set line gets a different color, so that the
     * player can better see where the last line was drawn.
     */
    var lastSetLine: Line? = null

    init {

        val widthInBoxes = boardSize.widthInBoxes
        val heightInBoxes = boardSize.heightInBoxes

        boxesArray = Array(widthInBoxes) { arrayOfNulls<Box?>(heightInBoxes) }

        /*
         * First create all boxes and insert them into the array and the lists.
         */
        for (gridX in 0 until widthInBoxes) {
            for (gridY in 0 until heightInBoxes) {

                val box = Box(gridX, gridY)

                boxesArray[box.gridX][box.gridY] = box

                _boxesList.add(box)
                openBoxes.add(box)
            }
        }

        /*
         * Now establish the relationships between the boxes to determine
         * the shared lines.
         */
        for (box in _boxesList) {

            val gridX = box.gridX
            val gridY = box.gridY

            /* To the right */

            var boxRight: Box? = null

            if (gridX < widthInBoxes - 1)
                boxRight = boxesArray[gridX + 1][gridY]

            if (boxRight != null) {

                val lineRight = Line(null, null, box, boxRight)

                box.rightLine = lineRight
                boxRight.leftLine = lineRight
                linesWithoutOwner.add(lineRight)
            }

            /* Downwards */

            var boxBelow: Box? = null

            if (gridY < heightInBoxes - 1)
                boxBelow = boxesArray[gridX][gridY + 1]

            if (boxBelow != null) {

                val lineBelow = Line(box, boxBelow, null, null)

                box.bottomLine = lineBelow
                boxBelow.topLine = lineBelow
                linesWithoutOwner.add(lineBelow)
            }
        }
    }

    /**
     * Returns the box at the given grid position.
     *
     * @throws IllegalArgumentException if the position is outside the grid.
     */
    fun getBox(gridX: Int, gridY: Int): Box {

        require(isInsideGrid(gridX, gridY)) {
            "The box lies outside the grid: " +
                "$gridX >= $widthInBoxes || $gridY >= $heightInBoxes"
        }

        return boxesArray[gridX][gridY]!!
    }

    /**
     * True when the given grid position is inside the board.
     */
    fun isInsideGrid(gridX: Int, gridY: Int) =
        gridX in 0 until widthInBoxes && gridY in 0 until heightInBoxes

    /**
     * Closes all boxes that can be closed.
     *
     * @param ownerToAssign The owner to assign to these boxes.
     * @return Could at least one box be closed? (Important for the game flow)
     */
    private fun closeAllPossibleBoxes(ownerToAssign: Player): Boolean {

        var atLeastOneBoxCouldBeClosed = false

        val openBoxesIterator = openBoxes.iterator()

        while (openBoxesIterator.hasNext()) {

            val box = openBoxesIterator.next()

            if (box.hasAllLinesWithOwner && box.owner == null) {

                box.owner = ownerToAssign

                openBoxesIterator.remove()

                atLeastOneBoxCouldBeClosed = true
            }
        }

        return atLeastOneBoxCouldBeClosed
    }

    /**
     * True when every box has an owner, i.e. the game is over.
     */
    fun allBoxesHaveOwner() = openBoxes.isEmpty()

    /**
     * True while there are still lines without an owner.
     */
    fun hasFreeLines() = linesWithoutOwner.isNotEmpty()

    /**
     * Assigns the line to the player and closes all boxes that are now
     * complete.
     *
     * @return Could the player close at least one box? (Important for the game flow)
     */
    fun chooseLine(line: Line, player: Player): Boolean {

        line.owner = player

        linesWithoutOwner.remove(line)

        lastSetLine = line

        return closeAllPossibleBoxes(player)
    }

    /**
     * Determines a good line for the computer move.
     *
     * The computer always completes a box when possible, otherwise it tries
     * random lines that do not hand the opponent a box.
     *
     * @param random The random source used for the move selection.
     */
    fun findGoodLineForComputerMove(random: Random): Line {

        /*
         * If a box can be closed somewhere, that should of course always
         * happen. Anything else would be very stupid.
         */
        findLastOpenLineForBox()?.let {
            return it
        }

        /*
         * If no box can be closed anywhere, we now randomly try lines and
         * make sure that we do not give away points. If we have not found
         * anything after 30 attempts, it must be so.
         */

        var randomLine = findRandomLine(random)

        var loopCounter = 0

        while (randomLine.couldCompleteAdjacentBox()) {

            randomLine = findRandomLine(random)

            if (++loopCounter >= NUMBER_OF_AI_ATTEMPTS)
                break
        }

        return randomLine
    }

    private fun findLastOpenLineForBox(): Line? {

        for (box in openBoxes)
            if (box.linesWithoutOwner.size == 1)
                return box.linesWithoutOwner[0]

        return null
    }

    private fun findRandomLine(random: Random): Line {

        check(hasFreeLines()) { "There are no free lines left." }

        val linesWithoutOwner = linesWithoutOwner.toList()

        val randomNumber = random.nextInt(linesWithoutOwner.size)

        return linesWithoutOwner[randomNumber]
    }

    /**
     * Counts the boxes owned by the player.
     */
    fun countPoints(player: Player): Int {

        var points = 0

        for (box in _boxesList)
            if (box.owner == player)
                points++

        return points
    }

    companion object {
        private const val NUMBER_OF_AI_ATTEMPTS = 30
    }
}
