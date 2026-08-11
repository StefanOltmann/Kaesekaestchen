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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import de.stefan_oltmann.kaesekaestchen.ui.theme.KaesekaestchenTheme
import de.stefan_oltmann.kaesekaestchen.ui.theme.LocalAppColors

private val SCREENSHOT_WIDTH_DP = 400.dp
private val SCREENSHOT_HEIGHT_DP = 700.dp

/**
 * Renders a screen in a phone-like portrait area for screenshot tests.
 *
 * The frame carries the [SCREENSHOT_TAG] that the capture helper resolves,
 * so goldens have a portrait resolution close to the Android app.
 *
 * @param modifier The modifier applied to the frame.
 * @param darkTheme True to render the dark theme.
 * @param content The screen to render.
 */
@Composable
internal fun ScreenshotFrame(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {

    KaesekaestchenTheme(darkTheme = darkTheme) {

        Box(
            modifier = modifier
                .size(SCREENSHOT_WIDTH_DP, SCREENSHOT_HEIGHT_DP)
                .background(LocalAppColors.current.background)
                .testTag(SCREENSHOT_TAG)
        ) {

            content()
        }
    }
}
