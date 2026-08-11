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
package de.stefan_oltmann.kaesekaestchen

import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.Res
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.app_name
import de.stefan_oltmann.kaesekaestchen.icons.AppIcon
import de.stefan_oltmann.kaesekaestchen.icons.Logo
import de.stefan_oltmann.kaesekaestchen.ui.App
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension

/** Minimum window size below which the layout breaks. */
private const val MIN_WINDOW_BREITE_PX = 480

/** Minimum window height below which the layout breaks. */
private const val MIN_WINDOW_HOEHE_PX = 640

/**
 * Desktop entry point that configures the window and launches the UI.
 */
fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = rememberVectorPainter(AppIcon.Logo),
        state = rememberWindowState(width = 480.dp, height = 720.dp)
    ) {

        /*
         * The layout breaks if the window is too small.
         */
        window.minimumSize = Dimension(MIN_WINDOW_BREITE_PX, MIN_WINDOW_HOEHE_PX)

        App()
    }
}
