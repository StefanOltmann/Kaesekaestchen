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
package de.stefan_oltmann.kaesekaestchen.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * All colors of the application theme.
 *
 * The palette is small on purpose; every screen and component draws with one
 * of these colors, so the UI stays consistent and readable in both themes.
 *
 * @property background The background of the screens.
 * @property surface Elevated areas: the header band, the selected mode chip
 *   and the inactive slider track.
 * @property onSurface Text and icons drawn on the background or the surface.
 * @property primary Action color: buttons, the active slider track and the
 *   slider thumb.
 * @property onPrimary Text and icons drawn on the primary color.
 */
data class AppColors(
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val primary: Color,
    val onPrimary: Color
)

/** The light colors with gray tones, faithful to the original app. */
val LightAppColors = AppColors(
    background = Color.White,
    surface = Color(0xFFE9E9E9),
    onSurface = Color.Black,
    primary = Color(0xFF333333),
    onPrimary = Color.White
)

/**
 * The dark colors.
 *
 * The background is pitch black, matching the black border strokes of the
 * handcrafted icons from the original app.
 */
val DarkAppColors = AppColors(
    background = Color.Black,
    surface = Color(0xFF2E2E2E),
    onSurface = Color(0xFFF0F0F0),
    primary = Color(0xFFD6D6D6),
    onPrimary = Color(0xFF1A1A1A)
)

/** The colors of the current composition, provided by the theme. */
val LocalAppColors = compositionLocalOf { LightAppColors }

/**
 * The application theme.
 *
 * It provides the [AppColors] of the active theme and the default content
 * color, so text and icons follow the theme without extra parameters. The
 * dark theme follows the system setting automatically and can be pinned for
 * tests.
 *
 * @param darkTheme True for the dark theme; defaults to the system setting.
 * @param content The content to render in the theme.
 */
@Composable
fun KaesekaestchenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors = if (darkTheme) DarkAppColors else LightAppColors

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalContentColor provides colors.onSurface,
        content = content
    )
}

/**
 * The board drawing colors and all UI sizes of the app.
 *
 * Every size in the UI is defined here so that no dimension is scattered
 * over the screens. The board colors stay the same in both themes, faithful
 * to the original Android app.
 */
object AppTheme {

    /** The color of the frame and of lines with an owner. */
    val frame = Color(0xFF333333)

    /** The color of lines without an owner. */
    val lineWithoutOwner = Color(0xFFF8F8F8)

    /** The color of the most recently set line. */
    val lastSetLine = Color(0xFFCC0000)

    /** The outer margin of every screen. */
    val borderMargin = 32.dp

    /** The height of the primary action buttons. */
    val buttonHeight = 64.dp

    /** The side length of the back button. */
    val backButtonSize = 40.dp

    /** The side length of a game mode option chip. */
    val modeButtonSize = 128.dp

    /** The side length of the current player symbol. */
    val currentPlayerSymbolSize = 48.dp

    /** The side length of player symbols on the scoreboard. */
    val playerSymbolSize = 64.dp

    /** The corner radius of a game mode option chip. */
    val chipCornerRadius = 24.dp

    /** The padding around the symbol inside a game mode option chip. */
    val chipIconPadding = 16.dp

    /** The font size of the game screen title. */
    val titleTextSize = 24.sp

    /** The font size of regular screen text. */
    val textSize = 24.sp

    /** The font size of the game mode option labels. */
    val modeLabelTextSize = 16.sp

    /** The font size of the scoreboard vs separator. */
    val vsTextSize = 36.sp

    /** The padding inside the board area. */
    val boardPadding = 8.dp

    /** The padding between a box border and its owner symbol. */
    val ownerSymbolPadding = 4.dp

    /** The thickness of the board lines. */
    val lineThickness = 3.dp

    /** The side length of a corner dot. */
    val cornerDotSize = 3.dp

    /** The alpha of the selected game mode symbol. */
    const val selectedAlpha = 1.0f

    /** The alpha of a not selected game mode symbol. */
    const val dimmedAlpha = 0.1f
}
