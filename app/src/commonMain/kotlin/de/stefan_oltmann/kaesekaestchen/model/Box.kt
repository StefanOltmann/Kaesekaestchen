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
 * A single box on the [Board], identified by its grid position.
 */
class Box(
    val gridX: Int,
    val gridY: Int
) {

    /**
     * Once a player completes a box, they become its owner. Every owned box
     * counts as one point at the end of the game.
     */
    var owner: Player? = null

    /* The lines of this box */
    var topLine: Line? = null
    var bottomLine: Line? = null
    var leftLine: Line? = null
    var rightLine: Line? = null

    /**
     * All lines of this box that have no owner yet.
     */
    val linesWithoutOwner: List<Line>
        get() {

            val lines: MutableList<Line> = mutableListOf()

            topLine?.let { if (it.owner == null) lines.add(it) }
            bottomLine?.let { if (it.owner == null) lines.add(it) }
            leftLine?.let { if (it.owner == null) lines.add(it) }
            rightLine?.let { if (it.owner == null) lines.add(it) }

            return lines
        }

    /**
     * True when every line of this box has an owner.
     */
    val hasAllLinesWithOwner: Boolean
        get() = linesWithoutOwner.isEmpty()
}
