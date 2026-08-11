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

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import de.stefan_oltmann.kaesekaestchen.model.Board
import de.stefan_oltmann.kaesekaestchen.model.BoardSize
import de.stefan_oltmann.kaesekaestchen.model.Player
import java.util.Locale

/**
 * Test tag of the [ScreenshotFrame] that the capture helper resolves.
 */
internal const val SCREENSHOT_TAG = "screen"

/**
 * Locale and theme combinations every golden screenshot is captured in.
 *
 * Each parameter set pins the locale and the theme and carries the golden
 * suffixes, so golden file names end in `-en-light`, `-en-dark`,
 * `-de-light` or `-de-dark`.
 */
internal fun screenshotVariantParameters(): Collection<Array<Any>> = listOf(
    arrayOf(Locale.ENGLISH, "en", false, "light"),
    arrayOf(Locale.ENGLISH, "en", true, "dark"),
    arrayOf(Locale.GERMAN, "de", false, "light"),
    arrayOf(Locale.GERMAN, "de", true, "dark")
)

/**
 * Deterministic mid-game board shared by all screenshot tests.
 *
 * The moves are fixed, so the recorded pixels stay comparable across runs.
 */
internal fun advancedBoard(): Board {

    val board = Board(BoardSize.SMALL)

    board.chooseLine(board.getBox(0, 0).rightLine!!, Player.CHEESE)
    board.chooseLine(board.getBox(1, 0).rightLine!!, Player.MOUSE)
    board.chooseLine(board.getBox(2, 0).rightLine!!, Player.CHEESE)

    val box = board.getBox(1, 1)

    board.chooseLine(box.topLine!!, Player.MOUSE)
    board.chooseLine(box.leftLine!!, Player.MOUSE)
    board.chooseLine(box.bottomLine!!, Player.MOUSE)

    /* This closes the box for the mouse and stays the last line. */
    board.chooseLine(box.rightLine!!, Player.MOUSE)

    board.chooseLine(board.getBox(0, 2).rightLine!!, Player.CHEESE)
    board.chooseLine(board.getBox(2, 2).rightLine!!, Player.MOUSE)

    return board
}

/**
 * Capture the current screen as a golden screenshot.
 *
 * The relative path is resolved inside the golden screenshot directory, so
 * goldens end up in `src/jvmTest/screenshots`. The active
 * [ScreenshotTestRule] decides whether the capture is recorded, verified, or
 * only rendered as a smoke check.
 *
 * @param name Relative golden file name without the `.png` extension.
 */
internal fun SemanticsNodeInteractionsProvider.captureScreenshot(name: String) {

    val screenshotRule = ScreenshotRuleContext.current ?: return

    screenshotRule.capture(onNodeWithTag(SCREENSHOT_TAG).captureToImage(), name)
}
