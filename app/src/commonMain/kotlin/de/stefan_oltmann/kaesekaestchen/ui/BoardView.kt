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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import de.stefan_oltmann.kaesekaestchen.icons.AppIcon
import de.stefan_oltmann.kaesekaestchen.icons.CheeseSymbol
import de.stefan_oltmann.kaesekaestchen.icons.MouseSymbol
import de.stefan_oltmann.kaesekaestchen.model.Board
import de.stefan_oltmann.kaesekaestchen.model.Box
import de.stefan_oltmann.kaesekaestchen.model.Line
import de.stefan_oltmann.kaesekaestchen.model.Player
import de.stefan_oltmann.kaesekaestchen.ui.theme.AppTheme

/**
 * Renders the board and forwards taps on free lines.
 *
 * The drawing faithfully ports the original board view from the
 * Android app: shared lines are drawn once, border lines in the frame
 * color, the last drawn line in red, and corner dots as squares.
 *
 * @param board The board to render.
 * @param boardVersion Incremented on every change to force a redraw.
 * @param modifier The modifier applied to the board area.
 * @param onLineSelect Called with the line the player tapped on.
 */
@Composable
fun BoardView(
    board: Board,
    boardVersion: Int,
    modifier: Modifier = Modifier,
    onLineSelect: (Line) -> Unit = {}
) {

    val currentOnLineSelected by rememberUpdatedState(onLineSelect)

    var boardSize by remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current

    val paddingPx = with(density) { AppTheme.boardPadding.toPx() }
    val symbolPaddingPx = with(density) { AppTheme.ownerSymbolPadding.toPx() }
    val lineThicknessPx = with(density) { AppTheme.lineThickness.toPx() }
    val cornerDotSizePx = with(density) { AppTheme.cornerDotSize.toPx() }

    Box(
        modifier = modifier
            .onSizeChanged { boardSize = it }
            .pointerInput(board) {

                detectTapGestures { offset ->

                    val geometry = boardGeometry(
                        board = board,
                        area = Size(boardSize.width.toFloat(), boardSize.height.toFloat()),
                        paddingPx = paddingPx
                    )

                    findLineAtPosition(board, offset, geometry)?.let {
                        currentOnLineSelected(it)
                    }
                }
            }
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            val geometry = boardGeometry(board, size, paddingPx)

            for (box in board.boxes)

                drawBox(
                    box = box,
                    board = board,
                    pixelX = box.gridX * geometry.sideLength + paddingPx + geometry.offsetX,
                    pixelY = box.gridY * geometry.sideLength + paddingPx + geometry.offsetY,
                    geometry = geometry,
                    lineThicknessPx = lineThicknessPx,
                    cornerDotSizePx = cornerDotSizePx
                )
        }

        /*
         * The owner symbols are placed above the lines. The board version
         * is used as the key, so every move recreates the symbols.
         */
        key(boardVersion) {

            if (boardSize != IntSize.Zero) {

                val geometry = boardGeometry(
                    board = board,
                    area = Size(boardSize.width.toFloat(), boardSize.height.toFloat()),
                    paddingPx = paddingPx
                )

                OwnerSymbols(
                    board = board,
                    geometry = geometry,
                    symbolPaddingPx = symbolPaddingPx
                )
            }
        }
    }
}

/*
 * The owner symbols, placed inside their boxes.
 */
@Composable
private fun OwnerSymbols(
    board: Board,
    geometry: BoardGeometry,
    symbolPaddingPx: Float
) {

    for (box in board.boxes)
        OwnerSymbol(
            box = box,
            geometry = geometry,
            symbolPaddingPx = symbolPaddingPx
        )
}

/*
 * The owner symbol of one box, or nothing when the box has no owner yet.
 */
@Composable
private fun OwnerSymbol(
    box: Box,
    geometry: BoardGeometry,
    symbolPaddingPx: Float
) {

    val owner = box.owner ?: return

    val density = LocalDensity.current

    val symbolSize = with(density) { (geometry.sideLength - symbolPaddingPx * 2).toDp() }

    Image(
        imageVector = if (owner == Player.CHEESE) AppIcon.CheeseSymbol else AppIcon.MouseSymbol,
        contentDescription = null,
        modifier = Modifier
            .offset(
                x = with(density) {
                    (box.gridX * geometry.sideLength +
                        geometry.paddingPx + geometry.offsetX + symbolPaddingPx).toDp()
                },
                y = with(density) {
                    (box.gridY * geometry.sideLength +
                        geometry.paddingPx + geometry.offsetY + symbolPaddingPx).toDp()
                }
            )
            .size(symbolSize)
    )
}

/*
 * Draws one box: the lines and the corner dots.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBox(
    box: Box,
    board: Board,
    pixelX: Float,
    pixelY: Float,
    geometry: BoardGeometry,
    lineThicknessPx: Float,
    cornerDotSizePx: Float
) {

    val sideLength = geometry.sideLength

    if (box.topLine == null) {

        drawLine(
            color = AppTheme.frame,
            start = Offset(pixelX, pixelY),
            end = Offset(pixelX + sideLength, pixelY),
            strokeWidth = lineThicknessPx,
            cap = StrokeCap.Butt
        )
    }

    drawLine(
        color = lineColor(box.bottomLine, board),
        start = Offset(pixelX, pixelY + sideLength),
        end = Offset(pixelX + sideLength, pixelY + sideLength),
        strokeWidth = lineThicknessPx,
        cap = StrokeCap.Butt
    )

    if (box.leftLine == null) {

        drawLine(
            color = AppTheme.frame,
            start = Offset(pixelX, pixelY),
            end = Offset(pixelX, pixelY + sideLength),
            strokeWidth = lineThicknessPx,
            cap = StrokeCap.Butt
        )
    }

    drawLine(
        color = lineColor(box.rightLine, board),
        start = Offset(pixelX + sideLength, pixelY),
        end = Offset(pixelX + sideLength, pixelY + sideLength),
        strokeWidth = lineThicknessPx,
        cap = StrokeCap.Butt
    )

    /*
     * Corner dots
     *
     * Deliberately not circles, because squares simply look better.
     */

    drawRect(
        color = AppTheme.frame,
        topLeft = Offset(pixelX - cornerDotSizePx / 2, pixelY - cornerDotSizePx / 2),
        size = Size(cornerDotSizePx, cornerDotSizePx)
    )

    drawRect(
        color = AppTheme.frame,
        topLeft = Offset(
            pixelX + sideLength - cornerDotSizePx / 2,
            pixelY - cornerDotSizePx / 2
        ),
        size = Size(cornerDotSizePx, cornerDotSizePx)
    )

    drawRect(
        color = AppTheme.frame,
        topLeft = Offset(
            pixelX - cornerDotSizePx / 2,
            pixelY + sideLength - cornerDotSizePx / 2
        ),
        size = Size(cornerDotSizePx, cornerDotSizePx)
    )

    drawRect(
        color = AppTheme.frame,
        topLeft = Offset(
            pixelX + sideLength - cornerDotSizePx / 2,
            pixelY + sideLength - cornerDotSizePx / 2
        ),
        size = Size(cornerDotSizePx, cornerDotSizePx)
    )
}

private fun lineColor(
    line: Line?,
    board: Board
): Color {

    return if (line != null && line == board.lastSetLine)
        AppTheme.lastSetLine
    else if (line?.owner != null)
        AppTheme.frame
    else if (line != null)
        AppTheme.lineWithoutOwner
    else
        AppTheme.frame
}
