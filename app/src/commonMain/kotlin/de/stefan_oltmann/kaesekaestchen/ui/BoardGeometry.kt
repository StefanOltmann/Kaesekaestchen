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
import de.stefan_oltmann.kaesekaestchen.model.Box
import de.stefan_oltmann.kaesekaestchen.model.Line
import kotlin.math.min

/*
 * The hit rectangles for the four lines of a box, relative to the box
 * position and size.
 */
private const val QUARTER = 0.25f
private const val THREE_QUARTERS = 0.75f
private const val ONE_AND_A_QUARTER = 1.25f

/**
 * The layout values for the board inside a given area.
 */
internal data class BoardGeometry(
    val sideLength: Float,
    val offsetX: Float,
    val offsetY: Float,
    val paddingPx: Float
)

/**
 * Computes the layout values for the board inside the given area.
 *
 * The board is centered inside the area.
 *
 * @param board The board to lay out.
 * @param area The area the board is drawn in.
 * @param paddingPx The padding around the board.
 */
internal fun boardGeometry(
    board: Board,
    area: Size,
    paddingPx: Float
): BoardGeometry {

    val widthWithPadding = area.width - paddingPx * 2
    val heightWithPadding = area.height - paddingPx * 2

    val maxWidth = widthWithPadding / board.widthInBoxes
    val maxHeight = heightWithPadding / board.heightInBoxes

    val sideLength = min(maxWidth, maxHeight)

    val offsetX = (widthWithPadding - board.widthInBoxes * sideLength) / 2f
    val offsetY = (heightWithPadding - board.heightInBoxes * sideLength) / 2f

    return BoardGeometry(sideLength, offsetX, offsetY, paddingPx)
}

/**
 * Determines which line of the tapped box the user tapped on.
 *
 * For the box determination the fractional part is deliberately cut off
 * with toInt(). Rounding would select the wrong box.
 *
 * @param board The board the tap happened on.
 * @param tapOffset The tap position in the area of the board.
 * @param geometry The layout values of the board.
 * @return The free line under the tap, or null when the tap was not on
 *   a free line.
 */
internal fun findLineAtPosition(
    board: Board,
    tapOffset: Offset,
    geometry: BoardGeometry
): Line? {

    val eventX = tapOffset.x - geometry.paddingPx - geometry.offsetX
    val eventY = tapOffset.y - geometry.paddingPx - geometry.offsetY

    val gridX = (eventX / geometry.sideLength).toInt()
    val gridY = (eventY / geometry.sideLength).toInt()

    /*
     * If the user taps somewhere outside the board, the input is simply
     * ignored and must not lead to an error.
     */
    if (!board.isInsideGrid(gridX, gridY))
        return null

    val box = board.getBox(gridX, gridY)

    /*
     * If the box already has an owner, the input is ignored.
     */
    if (box.owner != null)
        return null

    return findLineAtPosition(box, tapOffset, geometry)
}

/*
 * Returns the line under the given position or null when the tap was
 * not on any line of the box.
 */
private fun findLineAtPosition(
    box: Box,
    tapOffset: Offset,
    geometry: BoardGeometry
): Line? {

    val boxX = box.gridX * geometry.sideLength + geometry.paddingPx + geometry.offsetX
    val boxY = box.gridY * geometry.sideLength + geometry.paddingPx + geometry.offsetY

    val sideLength = geometry.sideLength

    val topRect = box.topLine?.let {
        Rect(
            boxX + sideLength * QUARTER,
            boxY - sideLength * QUARTER,
            boxX + sideLength * THREE_QUARTERS,
            boxY + sideLength * QUARTER
        )
    }

    val bottomRect = box.bottomLine?.let {
        Rect(
            boxX + sideLength * QUARTER,
            boxY + sideLength * THREE_QUARTERS,
            boxX + sideLength * THREE_QUARTERS,
            boxY + sideLength * ONE_AND_A_QUARTER
        )
    }

    val leftRect = box.leftLine?.let {
        Rect(
            boxX - sideLength * QUARTER,
            boxY + sideLength * QUARTER,
            boxX + sideLength * QUARTER,
            boxY + sideLength * THREE_QUARTERS
        )
    }

    val rightRect = box.rightLine?.let {
        Rect(
            boxX + sideLength * THREE_QUARTERS,
            boxY + sideLength * QUARTER,
            boxX + sideLength * ONE_AND_A_QUARTER,
            boxY + sideLength * THREE_QUARTERS
        )
    }

    if (topRect != null && contains(topRect, tapOffset))
        return box.topLine

    if (bottomRect != null && contains(bottomRect, tapOffset))
        return box.bottomLine

    if (leftRect != null && contains(leftRect, tapOffset))
        return box.leftLine

    if (rightRect != null && contains(rightRect, tapOffset))
        return box.rightLine

    return null
}

private fun contains(rect: Rect, offset: Offset) =
    offset.x in rect.left..rect.right && offset.y in rect.top..rect.bottom

/*
 * A small immutable rectangle value.
 */
private data class Rect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)
