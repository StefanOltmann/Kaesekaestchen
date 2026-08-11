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

/**
 * The game principle is drawing lines to complete boxes. This class
 * represents such a line, which is shared between its adjacent boxes.
 */
class Line(
    topBox: Box?,
    bottomBox: Box?,
    leftBox: Box?,
    rightBox: Box?
) {

    /* The adjacent boxes, kept for iteration */
    private val boxes: MutableList<Box> = mutableListOf()

    init {
        topBox?.let { boxes.add(it) }
        bottomBox?.let { boxes.add(it) }
        leftBox?.let { boxes.add(it) }
        rightBox?.let { boxes.add(it) }
    }

    /**
     * A line has no owner at the beginning.
     */
    var owner: Player? = null

    /**
     * If one of the boxes around this line has only two free lines left,
     * then placing this line would leave that box with just one free line.
     * The opponent could then complete it. This would hand the opponent
     * a box.
     */
    fun couldCompleteAdjacentBox(): Boolean {

        for (box in boxes)
            if (box.linesWithoutOwner.size <= 2)
                return true

        return false
    }
}
