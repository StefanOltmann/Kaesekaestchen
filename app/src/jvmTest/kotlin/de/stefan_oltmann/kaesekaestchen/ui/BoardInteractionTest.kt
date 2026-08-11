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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import de.stefan_oltmann.kaesekaestchen.model.Board
import de.stefan_oltmann.kaesekaestchen.model.BoardSize
import de.stefan_oltmann.kaesekaestchen.model.Player
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val BOARD_TAG = "board"

/*
 * The size of the rendered board in dp.
 */
private const val BOARD_EDGE_DP = 400f

/*
 * The board padding in dp, must match the constant in BoardView.
 */
private const val BOARD_PADDING_DP = 8f

/**
 * Tests that a tap on the rendered board places the tapped line.
 */
class BoardInteractionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickingOnLinePlacesIt() {

        val board = Board(BoardSize.VERY_SMALL)

        var boardVersion by mutableIntStateOf(0)

        composeTestRule.setContent {

            Box(
                modifier = Modifier
                    .size(BOARD_EDGE_DP.dp)
                    .testTag(BOARD_TAG)
            ) {

                BoardView(
                    board = board,
                    boardVersion = boardVersion,
                    onLineSelect = { line ->
                        board.chooseLine(line, Player.CHEESE)
                        boardVersion++
                    }
                )
            }
        }

        val tapPosition = tapPositionOfBottomEdgeOfTopLeftBox(board)

        composeTestRule.onNodeWithTag(BOARD_TAG).performTouchInput {
            click(tapPosition)
        }

        composeTestRule.waitForIdle()

        assertEquals(Player.CHEESE, board.getBox(0, 0).bottomLine?.owner)

        /* The line next to it is still free. */
        assertNull(board.getBox(0, 1).bottomLine?.owner)
    }

    @Test
    fun clickingIntoBoxMiddleDoesNothing() {

        val board = Board(BoardSize.VERY_SMALL)

        var boardVersion by mutableIntStateOf(0)

        composeTestRule.setContent {

            Box(
                modifier = Modifier
                    .size(BOARD_EDGE_DP.dp)
                    .testTag(BOARD_TAG)
            ) {

                BoardView(
                    board = board,
                    boardVersion = boardVersion,
                    onLineSelect = { line ->
                        board.chooseLine(line, Player.CHEESE)
                        boardVersion++
                    }
                )
            }
        }

        val tapPosition = tapPositionOfBoxMiddle(board)

        composeTestRule.onNodeWithTag(BOARD_TAG).performTouchInput {
            click(tapPosition)
        }

        composeTestRule.waitForIdle()

        assertNull(board.getBox(0, 0).bottomLine?.owner)
    }

    /*
     * Computes the pixel position of the bottom edge center of the top
     * left box, independent of the test scene density.
     */
    private fun tapPositionOfBottomEdgeOfTopLeftBox(board: Board): Offset {

        val geometry = boardGeometry(
            board = board,
            area = androidx.compose.ui.geometry.Size(BOARD_EDGE_DP, BOARD_EDGE_DP),
            paddingPx = BOARD_PADDING_DP
        )

        val nodeSize = composeTestRule.onNodeWithTag(BOARD_TAG).fetchSemanticsNode().size

        val scale = nodeSize.width / BOARD_EDGE_DP

        val x = BOARD_PADDING_DP + geometry.offsetX + geometry.sideLength / 2
        val y = BOARD_PADDING_DP + geometry.offsetY + geometry.sideLength

        return Offset(x * scale, y * scale)
    }

    /*
     * Computes the pixel position of the middle of the top left box.
     */
    private fun tapPositionOfBoxMiddle(board: Board): Offset {

        val geometry = boardGeometry(
            board = board,
            area = androidx.compose.ui.geometry.Size(BOARD_EDGE_DP, BOARD_EDGE_DP),
            paddingPx = BOARD_PADDING_DP
        )

        val nodeSize = composeTestRule.onNodeWithTag(BOARD_TAG).fetchSemanticsNode().size

        val scale = nodeSize.width / BOARD_EDGE_DP

        val x = BOARD_PADDING_DP + geometry.offsetX + geometry.sideLength / 2
        val y = BOARD_PADDING_DP + geometry.offsetY + geometry.sideLength / 2

        return Offset(x * scale, y * scale)
    }
}
